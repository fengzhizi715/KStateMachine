package com.safframework.statemachine.utils.extension

import com.safframework.statemachine.domain.DestroyEvent
import com.safframework.statemachine.domain.StopEvent
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.statemachine.InternalStateMachine
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.runDelayingException

/**
 *
 * @FileName:
 *          com.safframework.statemachine.utils.extension.`StateMachine+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 14:43
 * @version: V1.0 <描述当前版本功能>
 */

internal fun StateMachine.checkNotDestroyed() = check(!isDestroyed) { "$this is already destroyed" }

fun StateMachine.stop() {

    checkNotDestroyed()
    if (!isRunning) return
    sendEvent(StopEvent)
}

fun StateMachine.destroy() {

    if (isDestroyed) return
    sendEvent(DestroyEvent)
}

fun StateMachine.restart() {
    stop()
    start()
}

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
    machineListeners.forEach {
        runDelayingException {
            it.apply(block)
        }
    }

fun StateMachine.allStates():List<IState> {

    val result = mutableListOf<IState>()

    this.states.let { result.addAll(it) }

    this.states.forEach {
        result.addAll(it.allSubStates())
    }

    return result
}