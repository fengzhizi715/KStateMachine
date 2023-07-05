package com.safframework.statemachine.v2.state

import com.safframework.statemachine.v2.InterceptorBlock
import com.safframework.statemachine.v2.ResolvedTransition
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.transition.InternalTransition
import com.safframework.statemachine.v2.transition.NoTransition
import com.safframework.statemachine.v2.transition.TransitionDirectionProducerPolicy

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.state.InternalState
 * @author: Tony Shen
 * @date: 2023/7/4 10:28
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalState: IState {

    override var parent: InternalState?

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

internal fun InternalState.requireParent() = requireNotNull(parent) { "Parent is not set" }

internal fun InternalState.stateNotify(block: InterceptorBlock) = interceptors.forEach { it.apply(block) }

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