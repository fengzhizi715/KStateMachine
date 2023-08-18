package com.safframework.statemachine.state

import com.safframework.statemachine.utils.StateInterceptorBlock
import com.safframework.statemachine.utils.ResolvedTransition
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.transition.InternalTransition
import com.safframework.statemachine.transition.NoTransition
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.InternalState
 * @author: Tony Shen
 * @date: 2023/7/4 10:28
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalState: IState {

    override val parent: IState? get() = internalParent

    val internalParent: InternalState?
    fun setParent(parent: InternalState)

    fun doEnter(transitionParams: TransitionParams<*>)

    fun doExit(transitionParams: TransitionParams<*>)

    fun afterChildFinished(finishedChild: InternalState, transitionParams: TransitionParams<*>)

    fun <E : Event> recursiveFindUniqueResolvedTransition(event: E): ResolvedTransition<E>?

    fun recursiveEnterInitialStates()

    fun recursiveEnterStatePath(path: MutableList<InternalState>, transitionParams: TransitionParams<*>)

    fun recursiveExit(transitionParams: TransitionParams<*>)

    fun recursiveStop()

    fun recursiveFillActiveStates(states: MutableSet<IState>, self: IState, selfIncluding: Boolean)
}

internal fun InternalState.isNeighbor(state: IState) = parent?.states?.contains(state) == true

internal fun InternalState.requireInternalParent() = requireNotNull(internalParent) { "$this parent is not set" }

internal fun InternalState.stateNotify(block: StateInterceptorBlock) = interceptors.forEach { it.apply(block) }

internal fun <E : Event> InternalState.findTransitionsByEvent(event: E): List<InternalTransition<E>> {
    val triggeringTransitions = transitions.filter { it.isMatchingEvent(event) }
    @Suppress("UNCHECKED_CAST")
    return triggeringTransitions as List<InternalTransition<E>>
}

internal fun <E : Event> InternalState.findUniqueResolvedTransition(event: E): ResolvedTransition<E>? {
    val policy = TransitionDirectionProducerPolicy.DefaultPolicy(event)
    val transitions = findTransitionsByEvent(event)
        .map { it to it.produceTargetStateDirection(policy) }
        .filter { it.second !is NoTransition }
    check(transitions.size <= 1) { "Multiple transitions match $event, $transitions in $this" }
    return transitions.singleOrNull()
}