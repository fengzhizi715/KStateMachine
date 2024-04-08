package com.safframework.statemachine.statemachine

import com.safframework.statemachine.utils.StateMachineBlock
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.visitors.Visitor
import java.lang.Exception
import kotlin.jvm.Throws

/**
 *
 * @FileName:
 *          com.safframework.statemachine.statemachine.StateMachine
 * @author: Tony Shen
 * @date: 2023/7/4 09:50
 * @version: V1.0 <描述当前版本功能>
 */
data class EventAndArgument<E : Event>(val event: E, val argument: Any?)

enum class ProcessingResult {
    /** Event was sent to [PendingEventHandler] */
    PENDING,

    /** Event was processed */
    PROCESSED,

    /** Event was ignored */
    IGNORED,
}

@DslMarker
annotation class StateMachineDslMarker

interface StateMachine: State {

    var logger: Logger
    var ignoredEventHandler: IgnoredEventHandler
    var pendingEventHandler: PendingEventHandler
    var exceptionListener: ExceptionListener
    val isRunning: Boolean
    val isDestroyed: Boolean
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
    @Throws(StateMachineException::class)
    fun sendEvent(event: Event, argument: Any? = null): ProcessingResult

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

        fun onTransitionComplete(transitionParams: TransitionParams<*>) = Unit

        /**
         * Notifies about state changes.
         */
        fun onStateChanged(newState: IState) = Unit

        /**
         * Notifies that state machine has stopped.
         */
        fun onStopped() = Unit

        /**
         * Notifies that state machine has destroyed.
         */
        fun onDestroyed() = Unit
    }

    /**
     * State machine uses this interface to support internal logging on different platforms
     */
    fun interface Logger {

        fun log(message: String)
    }

    fun interface IgnoredEventHandler {

        fun onIgnoredEvent(eventAndArgument: EventAndArgument<*>)
    }

    fun interface PendingEventHandler {

        fun onPendingEvent(eventAndArgument: EventAndArgument<*>)
    }

    fun interface ExceptionListener {

        fun onException(exception: Exception)
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

