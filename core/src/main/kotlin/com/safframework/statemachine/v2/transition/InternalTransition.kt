package com.safframework.statemachine.v2.transition

import com.safframework.statemachine.v2.TransitionAction
import com.safframework.statemachine.v2.TransitionActionBlock
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.state.InternalState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.transition.InternalTransition
 * @author: Tony Shen
 * @date: 2023/7/4 10:37
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalTransition<E : Event> : Transition<E> {

    override val sourceState: InternalState

    fun produceTargetStateDirection(policy: TransitionDirectionProducerPolicy<E>): TransitionDirection
}

internal fun InternalTransition<*>.transitionNotify(block: TransitionActionBlock) =
    listeners.forEach { it.apply(block) }