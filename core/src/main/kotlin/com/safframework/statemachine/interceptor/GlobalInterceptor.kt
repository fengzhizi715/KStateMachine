package com.safframework.statemachine.interceptor

import com.safframework.statemachine.state.State
import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.context.StateContext

/**
 * 状态机全局的拦截器
 * @FileName:
 *          com.safframework.statemachine.interceptor.GlobalInterceptor
 * @author: Tony Shen
 * @date: 2020-02-17 21:52
 * @version: V1.0 <描述当前版本功能>
 */
interface GlobalInterceptor {

    /**
     * 进入某个 State
     */
    fun stateEntered(state: State)

    /**
     * 离开某个 State
     */
    fun stateExited(state: State)

    /**
     * State 发生改变
     * @param from: 当前状态
     * @param to:   下一个状态
     */
    fun stateChanged(from: State, to: State)

    /**
     * 触发 Transition
     */
    fun transition(transition: Transition)

    /**
     * 准备开始 Transition
     */
    fun transitionStarted(transition: Transition)

    /**
     * Transition 结束
     */
    fun transitionEnded(transition: Transition)

    /**
     * 状态机异常的回调
     */
    fun stateMachineError(stateMachine: StateMachine, exception: Exception)

    /**
     * 监听状态机上下文
     */
    fun stateContext(stateContext: StateContext)
}