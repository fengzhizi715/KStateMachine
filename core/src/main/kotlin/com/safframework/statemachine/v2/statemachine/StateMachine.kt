package com.safframework.statemachine.v2.statemachine

import com.safframework.statemachine.v2.StateMachineBlock
import com.safframework.statemachine.v2.domain.ChildMode
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.state.State
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.visitors.Visitor

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.statemachine.StateMachine
 * @author: Tony Shen
 * @date: 2023/7/4 09:50
 * @version: V1.0 <描述当前版本功能>
 */
@DslMarker
annotation class StateMachineDslMarker

interface StateMachine: State {

    var logger: Logger
    var ignoredEventHandler: IgnoredEventHandler
    var pendingEventHandler: PendingEventHandler
    val isRunning: Boolean
    val machineListeners: Collection<Listener>

    fun <L : Listener> addListener(listener: L): L

    fun removeListener(listener: Listener)

    /**
     * Starts state machine
     */
    fun start()

    /**
     * Forces state machine to stop
     */
    fun stop()

    /**
     * Machine must be started to process events
     */
    fun sendEvent(event: Event, argument: Any? = null)

    fun log(lazyMessage: () -> String)

    override fun accept(visitor: Visitor) = visitor.visit(this)

    interface Listener {
        /**
         * Notifies that state machine started (entered initial state).
         */
        fun onStarted() = Unit

        /**
         * This method is called when transition is performed.
         * There might be many transitions from one state to another,
         * this method might be used to listen to all transitions in one place
         * instead of listening for each transition separately.
         */
        fun onTransition(transitionParams: TransitionParams<*>) = Unit

        /**
         * Notifies about state changes.
         */
        fun onStateChanged(newState: IState) = Unit

        /**
         * Notifies that state machine has stopped.
         */
        fun onStopped() = Unit
    }

    /**
     * State machine uses this interface to support internal logging on different platforms
     */
    fun interface Logger {

        fun log(message: String)
    }

    fun interface IgnoredEventHandler {

        fun onIgnoredEvent(event: Event, argument: Any?)
    }

    fun interface PendingEventHandler {

        fun onPendingEvent(pendingEvent: Event, argument: Any?)
    }
}

fun createStateMachine(
    name: String? = null,
    childMode: ChildMode = ChildMode.EXCLUSIVE,
    start: Boolean = true,
    init: StateMachineBlock
): StateMachine = StateMachineImpl(name, childMode).apply {
    init()
    if (start) start()
}

