package com.safframework.statemachine.transition

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.InternalState
import com.safframework.statemachine.statemachine.InternalStateMachine
import com.safframework.statemachine.statemachine.runDelayingException
import com.safframework.statemachine.utils.TransitionInterceptorBlock

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.InternalTransition
 * @author: Tony Shen
 * @date: 2023/7/4 10:37
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalTransition<E : Event> : Transition<E> {

    override val sourceState: InternalState

    fun produceTargetStateDirection(policy: TransitionDirectionProducerPolicy<E>): TransitionDirection
}

internal fun InternalTransition<*>.transitionNotify(block: TransitionInterceptorBlock) {
    val machine = sourceState.machine as InternalStateMachine

    interceptors.forEach {
        machine.runDelayingException {
            it.apply(block)
        }
    }
}
