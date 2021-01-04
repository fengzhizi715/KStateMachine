package com.safframework.statemachine.interceptor

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.transition.Transition

/**
 * 状态机的拦截器
 * @FileName:
 *          com.safframework.statemachine.interceptor.Interceptor
 * @author: Tony Shen
 * @date: 2020-02-20 11:12
 * @version: V1.0 <描述当前版本功能>
 */
interface Interceptor {

    fun enteringState(stateMachine: StateMachine, stateContext: StateContext)

    fun enteredState(stateMachine: StateMachine, stateContext: StateContext)
}