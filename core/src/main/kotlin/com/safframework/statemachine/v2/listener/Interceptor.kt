package com.safframework.statemachine.v2.listener

import com.safframework.statemachine.v2.domain.ChildMode
import com.safframework.statemachine.v2.state.IFinalState
import com.safframework.statemachine.v2.transition.TransitionParams

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.interceptor.Interceptor
 * @author: Tony Shen
 * @date: 2023/7/4 20:04
 * @version: V1.0 <描述当前版本功能>
 */
interface Interceptor {

    fun onEntry(transitionParams: TransitionParams<*>) = Unit

    fun onExit(transitionParams: TransitionParams<*>) = Unit

    /**
     * If child mode is [ChildMode.EXCLUSIVE] notifies that child [IFinalState] is entered.
     * If child mode is [ChildMode.PARALLEL] notifies that all children has finished.
     */
    fun onFinished(transitionParams: TransitionParams<*>) = Unit
}