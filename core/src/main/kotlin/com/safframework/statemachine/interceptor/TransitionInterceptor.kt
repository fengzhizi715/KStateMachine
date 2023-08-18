package com.safframework.statemachine.interceptor

import com.safframework.statemachine.transition.TransitionParams

/**
 *
 * @FileName:
 *          com.safframework.statemachine.interceptor.TransitionInterceptor
 * @author: Tony Shen
 * @date: 2023/8/18 12:27
 * @version: V1.0 <描述当前版本功能>
 */
interface TransitionInterceptor {

    fun onTriggered(transitionParams: TransitionParams<*>) = Unit

    fun onComplete(transitionParams: TransitionParams<*>) = Unit
}