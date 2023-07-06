package com.safframework.statemachine.utils.extension

import com.safframework.statemachine.StateTransitionsHelper
import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.DataState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.*
import com.safframework.statemachine.transition.EventMatcher.Companion.isInstanceOf

/**
 *
 * @FileName:
 *          com.safframework.statemachine.utils.extension.`StateTransitionsHelper+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 16:29
 * @version: V1.0 <描述当前版本功能>
 */
fun com.safframework.statemachine.StateTransitionsHelper.requireTransition(name: String) =
    requireNotNull(findTransition(name)) { "Transition $name not found" }

/**
 * Find transition by Event type. This might be used to start listening to transition after state machine setup.
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.findTransition(): Transition<E>? {
    @Suppress("UNCHECKED_CAST")
    return transitions.find { it.eventMatcher.eventClass == E::class } as Transition<E>?
}

/**
 * Require transition by Event type
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.requireTransition() =
    requireNotNull(findTransition<E>()) { "Transition for ${E::class.simpleName} not found" }

/**
 * Shortcut overload for transition with an optional target state
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.transition(
    name: String? = null,
    targetState: State? = null
): Transition<E> = addTransition(DefaultTransition(name, isInstanceOf(), toState(), targetState))

/**
 * Creates transition.
 * You can specify guard function. Such guarded transition is triggered only when guard function returns true.
 *
 * This is a special kind of conditional transition but with simpler syntax and less flexibility.
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.transition(
    name: String? = null,
    block: UnitGuardedTransitionBuilder<E>.() -> Unit,
): Transition<E> {
    val builder = UnitGuardedTransitionBuilder<E>(name, toState()).apply {
        eventMatcher = isInstanceOf()
        block()
    }
    return addTransition(builder.build())
}

/**
 * This is more powerful version of [transition] function.
 * Here target state is a lambda which returns desired State.
 * This allows to use lateinit state variables for recursively depending states and
 * choose target state depending on application business logic.
 *
 * This is a special kind of conditional transition but with simpler syntax and less flexibility.
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.transitionOn(
    name: String? = null,
    block: UnitGuardedTransitionOnBuilder<E>.() -> Unit,
): Transition<E> {
    val builder = UnitGuardedTransitionOnBuilder<E>(name, toState()).apply {
        eventMatcher = isInstanceOf()
        block()
    }
    return addTransition(builder.build())
}

/**
 * Creates conditional transition. Caller should specify lambda which calculates [TransitionDirection].
 * For example target state may be different depending on some condition.
 */
inline fun <reified E : Event> com.safframework.statemachine.StateTransitionsHelper.transitionConditionally(
    name: String? = null,
    block: ConditionalTransitionBuilder<E>.() -> Unit,
): Transition<E> {
    val builder = ConditionalTransitionBuilder<E>(name, toState()).apply {
        eventMatcher = isInstanceOf()
        block()
    }
    return addTransition(builder.build())
}

/**
 * Shortcut function for type safe argument transition.
 * Data transition can not be targetless as it does not make sense.
 */
inline fun <reified E : DataEvent<D>, D> com.safframework.statemachine.StateTransitionsHelper.dataTransition(
    name: String? = null,
    targetState: DataState<D>
): Transition<E> {
    require(targetState != toState()) {
        "data transition should no be self targeted, use simple transition instead"
    }
    return addTransition(DefaultTransition(name, isInstanceOf(), toState(), targetState))
}

/**
 * Creates type safe argument transition to [DataState].
 */
inline fun <reified E : DataEvent<D>, D> com.safframework.statemachine.StateTransitionsHelper.dataTransition(
    name: String? = null,
    block: DataGuardedTransitionBuilder<E, D>.() -> Unit,
): Transition<E> {
    val builder = DataGuardedTransitionBuilder<E, D>(name, toState()).apply {
        eventMatcher = isInstanceOf()
        block()
    }
    requireNotNull(builder.targetState) {
        "data transition should no be targetless, specify targetState or use simple transition instead"
    }
    require(builder.targetState != toState()) {
        "data transition should no be self targeted, use simple transition instead"
    }
    return addTransition(builder.build())
}

/**
 * Data transition, otherwise same as [transitionOn]
 */
inline fun <reified E : DataEvent<D>, D> com.safframework.statemachine.StateTransitionsHelper.dataTransitionOn(
    name: String? = null,
    block: DataGuardedTransitionOnBuilder<E, D>.() -> Unit,
): Transition<E> {
    val builder = DataGuardedTransitionOnBuilder<E, D>(name, toState()).apply {
        eventMatcher = isInstanceOf()
        block()
    }
    return addTransition(builder.build())
}