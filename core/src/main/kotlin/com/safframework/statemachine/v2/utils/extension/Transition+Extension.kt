package com.safframework.statemachine.v2.utils.extension

import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.utils.extension.`Transition+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 11:58
 * @version: V1.0 <描述当前版本功能>
 */
inline fun <reified E : Event> Transition<E>.onTriggered(crossinline block: (TransitionParams<E>) -> Unit) {
    addListener(object : Transition.Listener {
        @Suppress("UNCHECKED_CAST")
        override fun onTriggered(transitionParams: TransitionParams<*>) = block(transitionParams as TransitionParams<E>)
    })
}