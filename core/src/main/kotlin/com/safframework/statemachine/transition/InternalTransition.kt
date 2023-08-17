package com.safframework.statemachine.transition

import com.safframework.statemachine.utils.TransitionActionBlock
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.InternalState

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

internal fun InternalTransition<*>.transitionNotify(block: TransitionActionBlock) =
    actions.forEach { it.apply(block) }