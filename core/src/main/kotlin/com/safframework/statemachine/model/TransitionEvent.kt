package com.safframework.statemachine.model

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.model.TransitionEvent
 * @author: Tony Shen
 * @date: 2020-02-20 16:18
 * @version: V1.0 <描述当前版本功能>
 */
sealed class TransitionEvent {

    data class EnterTransition(val stateMachine: StateMachine, val stateContext: StateContext) : TransitionEvent()

    data class ExitTransition(val stateMachine: StateMachine, val stateContext: StateContext) : TransitionEvent()
}