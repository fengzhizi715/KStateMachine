package com.safframework.statemachine.statemachine

import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.domain.DestroyEvent
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.domain.StopEvent
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.state.DefaultState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.InternalState
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.transition.transitionNotify
import com.safframework.statemachine.utils.extension.machineNotify
import com.safframework.statemachine.visitors.CheckUniqueNamesVisitor
import kotlin.jvm.Throws
import kotlin.reflect.KClass

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
        set(value) {
            checkPropertyNotMutedOnRunningMachine(StateMachine.Logger::class)
            field = value
        }

    override var ignoredEventHandler: StateMachine.IgnoredEventHandler = DefaultIgnoredEventHandlerImpl(this)
        set(value) {
            checkPropertyNotMutedOnRunningMachine(StateMachine.IgnoredEventHandler::class)
            field = value
        }

    override var pendingEventHandler: StateMachine.PendingEventHandler = QueuePendingEventHandlerImpl(this)
        set(value) {
            checkPropertyNotMutedOnRunningMachine(StateMachine.PendingEventHandler::class)
            field = value
        }

    override var exceptionListener: StateMachine.ExceptionListener = DefaultExceptionListener
        set(value) {
            checkPropertyNotMutedOnRunningMachine(StateMachine.ExceptionListener::class)
            field = value
        }

    private var _isDestroyed: Boolean = false
    override val isDestroyed get() = _isDestroyed

    /**
     * Help to check that [processEvent] is not called from state machine notification method.
     * Access to this field must be thread safe.
     */
    private var isProcessingEvent = false

    private var _isRunning = false
    override val isRunning get() = _isRunning

    private var delayedListenerException: Exception? = null

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

    override fun delayListenerException(exception: Exception) {
        if (delayedListenerException == null)
            delayedListenerException = exception
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

    private fun doStop() {
        _isRunning = false
        recursiveStop()
        log { "$this stopped" }
        machineNotify { onStopped() }
    }

    private  fun doDestroy() {
        _isDestroyed = true
        machineNotify { onDestroyed() }
        log { "$this destroyed" }
    }

    @Synchronized
    @Throws(StateMachineException::class)
    override fun sendEvent(event: Event, argument: Any?): ProcessingResult {
        checkNotDestroyed()
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
                    if (isDestroyed || !isRunning) { // if it happens while event processing
                        it.clear()
                        return result
                    }
                    process(eventAndArgument)

                    eventAndArgument = it.nextEventAndArgument()
                }
            }
        } catch (e: Exception) {
            queue?.clear()
            throw StateMachineException(e.message?:"")
        } finally {
            isProcessingEvent = false
        }

        return result
    }

    private fun process(eventAndArgument: EventAndArgument<*>): ProcessingResult {

        val eventProcessed = runCheckingExceptions{

            when (val event = eventAndArgument.event) {
                is StopEvent -> {
                    doStop()
                    true
                }
                is DestroyEvent -> {
                    if (isRunning) doStop()
                    doDestroy()
                    true
                }
                else -> doProcessEvent(eventAndArgument)
            }
        }

        if (!eventProcessed) {
            ignoredEventHandler.onIgnoredEvent(eventAndArgument)
            return ProcessingResult.IGNORED
        }

        return ProcessingResult.PROCESSED
    }

    private fun <E : Event> doProcessEvent(eventAndArgument: EventAndArgument<E>): Boolean {
        val (event, argument) = eventAndArgument
        if (isFinished) {
            log { "$this is finished, skipping event $event, with argument $argument" }
            return false
        }

        val (transition, direction) = recursiveFindUniqueResolvedTransition(event) ?: return false

        val transitionParams = TransitionParams(transition, direction, event, argument)

        val targetState = direction.targetState as? InternalState ?: return false

        val targetText = "to $targetState"

        if (event is DataEvent<*>) {
            log { "event:${event::class.simpleName}(${event.data}), argument:${argument}, triggers $transition from ${transition.sourceState} $targetText" }
        } else {
            log { "event:${event::class.simpleName}, argument:${argument}, triggers $transition from ${transition.sourceState} $targetText" }
        }

        transition.transitionNotify { this.onTriggered(transitionParams) }
        machineNotify {
            onTransition(transitionParams)
        }

        switchToTargetState(targetState, transition.sourceState, transitionParams)
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

    private fun <R> runCheckingExceptions(block: () -> R): R {
        val result: R
        try {
            result = block()
        } catch (e: Exception) {
            log { "Fatal exception happened, $this machine is in unpredictable state and will be destroyed: $e" }
            runCatching { doDestroy() }
            throw e
        }
        delayedListenerException?.let {
            delayedListenerException = null
            exceptionListener.onException(it)
        }
        return result
    }
}

internal inline fun InternalStateMachine.runDelayingException(crossinline block: () -> Unit) =
    try {
        block()
    } catch (e: Exception) {
        delayListenerException(e)
    }

private fun StateMachine.checkPropertyNotMutedOnRunningMachine(propertyType: KClass<*>) =
    check(!isRunning) { "Can not change ${propertyType.simpleName} after state machine started" }