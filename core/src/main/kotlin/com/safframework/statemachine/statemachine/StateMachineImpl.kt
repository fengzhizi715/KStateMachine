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
    override var ignoredEventHandler = StateMachine.IgnoredEventHandler { eventAndArgument ->
        val event = eventAndArgument.event
        if (event is DataEvent<*>) {
            log { "$this ignored ${event::class.simpleName}(${event.data})" }
        } else {
            log { "$this ignored ${event::class.simpleName}" }
        }
    }
    override var pendingEventHandler: StateMachine.PendingEventHandler = QueuePendingEventHandlerImpl(this)

    override var exceptionListener = StateMachine.ExceptionListener {
        throw it
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
    override fun sendEvent(event: Event, argument: Any?): ProcessingResult {
        check(isRunning) { "$this is not started, call start() first" }

        val eventAndArgument = EventAndArgument(event, argument)

        if (isProcessingEvent) {
            pendingEventHandler.onPendingEvent(eventAndArgument)

            return ProcessingResult.PENDING
        }

        val queue = pendingEventHandler as? QueuePendingEventHandler
        queue?.checkEmpty()

        isProcessingEvent = true

        var result: ProcessingResult

        try {
            result = process(eventAndArgument)

            queue?.let {
                var eventAndArgument = it.nextEventAndArgument()
                while (eventAndArgument != null) {
                    if (!isRunning) { // if it happens while event processing
                        it.clear()
                        return result
                    }
                    process(eventAndArgument)

                    eventAndArgument = it.nextEventAndArgument()
                }
            }
        } catch (e: Exception) {
            queue?.clear()
            throw e
        } finally {
            isProcessingEvent = false
        }

        return result
    }

    private fun process(eventAndArgument: EventAndArgument<*>): ProcessingResult {
        val eventProcessed = doProcessEvent(eventAndArgument)

        if (!eventProcessed) {
            ignoredEventHandler.onIgnoredEvent(eventAndArgument)
        }
        return if (eventProcessed) ProcessingResult.PROCESSED else ProcessingResult.IGNORED
    }

    private fun <E : Event> doProcessEvent(eventAndArgument: EventAndArgument<E>): Boolean {
        val (event, argument) = eventAndArgument
        if (isFinished) {
            log { "$this is finished, skipping event $event, with argument $argument" }
            return false
        }

        val (transition, direction) = recursiveFindUniqueResolvedTransition(event) ?: return false

        val transitionParams = TransitionParams(transition, direction, event, argument)

        val targetState = direction.targetState as? InternalState

        val targetText = if (targetState != null) "to $targetState" else "[targetless]"

        if (event is DataEvent<*>) {
            log { "event:${event::class.simpleName}(${event.data}), argument:${argument}, triggers $transition from ${transition.sourceState} $targetText" }
        } else {
            log { "event:${event::class.simpleName}, argument:${argument}, triggers $transition from ${transition.sourceState} $targetText" }
        }

        transition.transitionNotify { this.onTriggered(transitionParams) }
        machineNotify {
            onTransition(transitionParams)
        }

        targetState?.let { switchToTargetState(it, transition.sourceState, transitionParams) }
        transition.transitionNotify { this.onComplete(transitionParams) }
        machineNotify { onTransitionComplete(transitionParams) }

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
