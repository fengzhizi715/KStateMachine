package com.safframework.statemachine.utils.extension

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.interceptor.TransitionInterceptor
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.utils.extension.`Transition+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 11:58
 * @version: V1.0 <描述当前版本功能>
 */
inline fun <reified E : Event> Transition<E>.onAction(crossinline block: (TransitionParams<E>) -> Unit) {
    addTransitionInterceptor(object : TransitionInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun onTriggered(transitionParams: TransitionParams<*>) = block(transitionParams as TransitionParams<E>)
    })
}