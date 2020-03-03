package com.safframework.statemachine.interceptor

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.interceptor.Interceptor
 * @author: Tony Shen
 * @date: 2020-02-20 11:12
 * @version: V1.0 <描述当前版本功能>
 */
interface Interceptor {

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