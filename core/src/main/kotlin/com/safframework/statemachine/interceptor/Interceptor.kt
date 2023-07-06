package com.safframework.statemachine.interceptor

import com.safframework.statemachine.domain.ChildMode
import com.safframework.statemachine.state.IFinalState
import com.safframework.statemachine.transition.TransitionParams

/**
 *
 * @FileName:
 *          com.safframework.statemachine.interceptor.Interceptor
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