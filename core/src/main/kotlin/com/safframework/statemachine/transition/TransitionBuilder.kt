package com.safframework.statemachine.transition

import com.safframework.statemachine.utils.Guard
import com.safframework.statemachine.utils.TransitionDirectionProducer
import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.interceptor.TransitionInterceptor
import com.safframework.statemachine.state.DataState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachineDslMarker
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy.CollectTargetStatesPolicy
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy.DefaultPolicy
import com.safframework.statemachine.utils.TransitionParamsBlock

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.TransitionBuilder
 * @author: Tony Shen
 * @date: 2023/7/4 16:36
 * @version: V1.0 <描述当前版本功能>
 */
@StateMachineDslMarker
abstract class TransitionBuilder<E : Event>(protected val name: String?, protected val sourceState: IState) {
    var interceptors = mutableListOf<TransitionInterceptor>()
    lateinit var eventMatcher: EventMatcher<E>
    var type = TransitionType.LOCAL

    abstract fun build(): Transition<E>
}

abstract class BaseGuardedTransitionBuilder<E : Event>(name: String?, sourceState: IState) :
    TransitionBuilder<E>(name, sourceState) {
    var guard: Guard<E> = { true }
}

abstract class GuardedTransitionBuilder<E : Event, S : IState>(name: String?, sourceState: IState) :
    BaseGuardedTransitionBuilder<E>(name, sourceState) {
    var targetState: S? = null

    override fun build(): Transition<E> {
        val direction: TransitionDirectionProducer<E> = {
            when (it) {
                is DefaultPolicy<E> ->
                    if (guard(it.event)) {
                        val target = targetState
                        if (target == null) stay() else targetState(target)
                    } else {
                        noTransition()
                    }
                is CollectTargetStatesPolicy<E> -> {
                    val target = targetState
                    if (target == null) stay() else targetState(target)
                }
            }
        }

        val transition = DefaultTransition(name, eventMatcher, type, sourceState, direction)
        interceptors.forEach { transition.addTransitionInterceptor(it) }
        return transition
    }
}

abstract class GuardedTransitionOnBuilder<E : Event, S : IState>(name: String?, sourceState: IState) :
    BaseGuardedTransitionBuilder<E>(name, sourceState) {
    lateinit var targetState: (E) -> S

    override fun build(): Transition<E> {
        val direction: TransitionDirectionProducer<E> = {
            when (it) {
                is DefaultPolicy<E> -> if (guard(it.event)) targetState(targetState(it.event)) else noTransition()
                is CollectTargetStatesPolicy<E> -> noTransition()
            }
        }

        val transition = DefaultTransition(name, eventMatcher, type, sourceState, direction)
        interceptors.forEach { transition.addTransitionInterceptor(it) }
        return transition
    }
}

class ConditionalTransitionBuilder<E : Event>(name: String?, sourceState: IState) :
    TransitionBuilder<E>(name, sourceState) {
    lateinit var direction: (E) -> TransitionDirection

    override fun build(): Transition<E> {
        val direction: TransitionDirectionProducer<E> = {
            when (it) {
                is DefaultPolicy<E> -> direction(it.event)
                is CollectTargetStatesPolicy<E> -> noTransition()
            }
        }

        val transition = DefaultTransition(name, eventMatcher, type, sourceState, direction)
        interceptors.forEach { transition.addTransitionInterceptor(it) }
        return transition
    }
}

/**
 * Any [Event] (with any data) can lead to [State]
 */
class UnitGuardedTransitionBuilder<E : Event>(name: String?, sourceState: IState) :
    GuardedTransitionBuilder<E, State>(name, sourceState)

class UnitGuardedTransitionOnBuilder<E : Event>(name: String?, sourceState: IState) :
    GuardedTransitionOnBuilder<E, State>(name, sourceState)

/**
 * Type safe argument transition builder
 */
class DataGuardedTransitionBuilder<E : DataEvent<D>, D>(name: String?, sourceState: IState) :
    GuardedTransitionBuilder<E, DataState<D>>(name, sourceState)

/**
 * Type safe argument transitionOn builder
 */
class DataGuardedTransitionOnBuilder<E : DataEvent<D>, D>(name: String?, sourceState: IState) :
    GuardedTransitionOnBuilder<E, DataState<D>>(name, sourceState)

inline fun <reified E : Event> TransitionBuilder<E>.action(crossinline block: TransitionParamsBlock<E>): TransitionInterceptor{

    return object : TransitionInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun onTriggered(transitionParams: TransitionParams<*>) = block(transitionParams as TransitionParams<E>)
    }.also { interceptors.add(it) }
}

inline fun <reified E : Event> TransitionBuilder<E>.complete(crossinline block: TransitionParamsBlock<E>): TransitionInterceptor{

    return object : TransitionInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun onComplete(transitionParams: TransitionParams<*>) = block(transitionParams as TransitionParams<E>)
    }.also { interceptors.add(it) }
}

@Suppress("UNUSED") // The unused warning is probably a bug
inline fun <reified E : Event> TransitionBuilder<E>.isInstanceOf() = EventMatcher.isInstanceOf<E>()

@Suppress("UNUSED") // The unused warning is probably a bug
inline fun <reified E : Event> TransitionBuilder<E>.isEqual() = object : EventMatcher<E>(E::class) {
    override fun match(value: Event) = value::class == E::class
}