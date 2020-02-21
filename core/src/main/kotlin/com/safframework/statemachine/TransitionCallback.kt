package com.safframework.statemachine

import com.safframework.statemachine.model.BaseState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TransitionCallback
 * @author: Tony Shen
 * @date: 2020-02-20 11:12
 * @version: V1.0 <描述当前版本功能>
 */
interface TransitionCallback {

    fun enteringState(
        stateMachine: StateMachine,
        currentState: BaseState,
        transition: Transition,
        targetState: BaseState
    )

    fun enteredState(
        stateMachine: StateMachine,
        previousState: BaseState,
        transition: Transition,
        currentState: BaseState
    )
}