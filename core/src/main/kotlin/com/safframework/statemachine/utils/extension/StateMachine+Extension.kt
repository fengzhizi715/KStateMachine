package com.safframework.statemachine.utils.extension

import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.statemachine.InternalStateMachine
import com.safframework.statemachine.statemachine.StateMachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.utils.extension.`StateMachine+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 14:43
 * @version: V1.0 <描述当前版本功能>
 */
fun StateMachine.started(block: StateMachine.() -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStarted() = block()
    })
}

fun StateMachine.stopped(block: StateMachine.() -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStopped() = block()
    })
}

fun StateMachine.transition(block: StateMachine.(TransitionParams<*>) -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onTransition(transitionParams: TransitionParams<*>) =
            block(transitionParams)
    })
}

fun StateMachine.transitionComplete(block: StateMachine.(TransitionParams<*>) -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onTransitionComplete(transitionParams: TransitionParams<*>) =
            block(transitionParams)
    })
}

fun StateMachine.stateChanged(block: StateMachine.(newState: IState) -> Unit) {
    addListener(object : StateMachine.Listener {
        override fun onStateChanged(newState: IState) = block(newState)
    })
}

fun InternalStateMachine.machineNotify(block: StateMachine.Listener.() -> Unit) =
    machineListeners.forEach { it.apply(block) }

fun StateMachine.allStates():List<IState> {

    val result = mutableListOf<IState>()

    this.states.let { result.addAll(it) }

    this.states.forEach {
        result.addAll(it.allSubStates())
    }

    return result
}