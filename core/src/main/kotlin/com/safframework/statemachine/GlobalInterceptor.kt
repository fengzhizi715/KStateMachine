package com.safframework.statemachine

/**
 * 状态机全局的拦截器，对 State 改变前、后进行监听拦截。
 * @FileName:
 *          com.safframework.statemachine.GlobalInterceptor
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
    fun stateChanged(from: State,to: State)
}