package com.safframework.statemachine.v2.utils.extension

import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.statemachine.InternalStateMachine
import com.safframework.statemachine.v2.statemachine.StateMachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.utils.extension.`StateMachine+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 14:43
 * @version: V1.0 <描述当前版本功能>
 */
fun StateMachine.onStarted(block: StateMachine.() -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStarted() = block()
    })
}

fun StateMachine.onStopped(block: StateMachine.() -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStopped() = block()
    })
}

fun StateMachine.onTransition(block: StateMachine.(TransitionParams<*>) -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onTransition(transitionParams: TransitionParams<*>) =
            block(transitionParams)
    })
}

fun StateMachine.onStateChanged(block: StateMachine.(newState: IState) -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStateChanged(newState: IState) = block(newState)
    })
}

fun InternalStateMachine.machineNotify(block: StateMachine.Listener.() -> Unit) =
    machineListeners.forEach { it.apply(block) }