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

    fun stateEntered(state: State)

    fun stateExited(state: State)
}