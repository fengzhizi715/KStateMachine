package com.safframework.statemachine.statemachine

import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.state.DefaultState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.InternalState
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.transition.transitionNotify
import com.safframework.statemachine.utils.extension.machineNotify
import com.safframework.statemachine.visitors.CheckUniqueNamesVisitor

/**
 *
 * @FileName:
 *          com.safframework.statemachine.statemachine.StateMachineImpl
 * @author: Tony Shen
 * @date: 2023/7/4 15:47
 * @version: V1.0 <描述当前版本功能>
 */
internal class StateMachineImpl(name: String?, childMode: ChildMode) :
    InternalStateMachine, DefaultState(name, childMode) {
    /** Access to this field must be thread safe. */
    private val _machineListeners = mutableSetOf<StateMachine.Listener>()
    override val machineListeners: Collection<StateMachine.Listener> get() = _machineListeners

    override var logger: StateMachine.Logger = NullLogger
    override var ignoredEventHandler = StateMachine.IgnoredEventHandler { _, _ -> }
    override var pendingEventHandler = StateMachine.PendingEventHandler { pendingEvent, _ ->
        error(
            "$this can not process pending $pendingEvent as event processing is already running. " +
                    "Do not call processEvent() from notification listeners."
        )
    }

    /**
     * Help to check that [processEvent] is not called from state machine notification method.
     * Access to this field must be thread safe.
     */
    private var isProcessingEvent = false

    private var _isRunning = false
    override val isRunning get() = _isRunning

    private object NullLogger : StateMachine.Logger {
        override fun log(message: String) {}
    }

    @Synchronized
    override fun <L : StateMachine.Listener> addListener(listener: L): L {
        require(_machineListeners.add(listener)) { "$listener is already added" }
        return listener
    }

    @Synchronized
    override fun removeListener(listener: StateMachine.Listener) {
        _machineListeners.remove(listener)
    }

    override fun start() {
        accept(CheckUniqueNamesVisitor())

        run(makeStartTransitionParams(this))
        recursiveEnterInitialStates()
    }

    override fun startFrom(state: IState) {
        val transitionParams = makeStartTransitionParams(this, state)
        run(transitionParams)
        switchToTargetState(state as InternalState, this, transitionParams)
    }

    private fun run(transitionParams: TransitionParams<*>) {
        check(!isRunning) { "$this is already started" }
        if (childMode == ChildMode.EXCLUSIVE)
            checkNotNull(initialState) { "Initial state is not set, call setInitialState() first" }

        _isRunning = true
        log { "$this started" }
        machineNotify { onStarted() }
        doEnter(transitionParams)
    }

    override fun stop() {
        _isRunning = false
        recursiveStop()
        log { "$this stopped" }
        machineNotify { onStopped() }
    }

    @Synchronized
    override fun sendEvent(event: Event, argument: Any?) {
        check(isRunning) { "$this is not started, call start() first" }

        if (isProcessingEvent)
            pendingEventHandler.onPendingEvent(event, argument)
        isProcessingEvent = true

        try {
            if (!doProcessEvent(event, argument)) {
                if (event is DataEvent<*>) {
                    log { "$this ignored ${event::class.simpleName}(${event.data})" }
                } else {
                    log { "$this ignored ${event::class.simpleName}" }
                }
                ignoredEventHandler.onIgnoredEvent(event, argument)
            }
        } finally {
            isProcessingEvent = false
        }
    }

    private fun doProcessEvent(event: Event, argument: Any?): Boolean {
        if (isFinished) {
            log { "$this is finished, skipping event $event, with argument $argument" }
            return false
        }

        val (transition, direction) = recursiveFindUniqueResolvedTransition(event) ?: return false

        val transitionParams = TransitionParams(transition, direction, event, argument)

        val targetState = direction.targetState as? InternalState

        val targetText = if (targetState != null) "to $targetState" else "[targetless]"

        if (event is DataEvent<*>) {
            log { "event:${event::class.simpleName}(${event.data}), triggers $transition from ${transition.sourceState} $targetText" }
        } else {
            log { "event:${event::class.simpleName}, triggers $transition from ${transition.sourceState} $targetText" }
        }

        transition.transitionNotify {
            invoke(transitionParams)
        }

        machineNotify {
            onTransition(transitionParams)
        }

        targetState?.let { switchToTargetState(it, transition.sourceState, transitionParams) }
        return true
    }

    override fun log(lazyMessage: () -> String) {
        if (logger != NullLogger)
            logger.log(lazyMessage())
    }

    /**
     *  Starts machine if its inner state machine
     */
    override fun doEnter(transitionParams: TransitionParams<*>) =
        if (!isRunning) start() else super.doEnter(transitionParams)
}
