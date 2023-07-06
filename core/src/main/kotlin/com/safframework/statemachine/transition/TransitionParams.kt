package com.safframework.statemachine.transition

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.statemachine.StateMachineDslMarker

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.TransitionParams
 * @author: Tony Shen
 * @date: 2023/7/4 10:00
 * @version: V1.0 <描述当前版本功能>
 */
@StateMachineDslMarker
data class TransitionParams<E : Event>(
    val transition: Transition<E>,
    val direction: TransitionDirection,
    val event: E,
    /**
     * This parameter may be used to pass arbitrary data with the event,
     * so there is no need to define [Event] subclasses every time.
     * Subclassing should be preferred if the event always contains data of some type.
     */
    val argument: Any? = null,
)
