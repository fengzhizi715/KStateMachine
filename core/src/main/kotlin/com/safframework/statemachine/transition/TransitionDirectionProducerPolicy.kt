package com.safframework.statemachine.transition

import com.safframework.statemachine.domain.Event

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.TransitionDirectionProducerPolicy
 * @author: Tony Shen
 * @date: 2023/7/4 11:10
 * @version: V1.0 <描述当前版本功能>
 */
sealed class TransitionDirectionProducerPolicy<E : Event> {
    class DefaultPolicy<E : Event>(val event: E) : TransitionDirectionProducerPolicy<E>()

    /**
     * TODO find the way to collect target states of conditional transitions
     */
    class CollectTargetStatesPolicy<E : Event> : TransitionDirectionProducerPolicy<E>()
}