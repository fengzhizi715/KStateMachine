package com.safframework.statemachine.v2.state

import com.safframework.statemachine.v2.ResolvedTransition
import com.safframework.statemachine.v2.StateBlock
import com.safframework.statemachine.v2.algorithm.TreeAlgorithm.findPathFromTargetToLca
import com.safframework.statemachine.v2.domain.ChildMode
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.domain.StartEvent
import com.safframework.statemachine.v2.interceptor.Interceptor
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.statemachine.InternalStateMachine
import com.safframework.statemachine.v2.statemachine.StateMachine
import com.safframework.statemachine.v2.transition.DefaultTransition
import com.safframework.statemachine.v2.transition.EventMatcher
import com.safframework.statemachine.v2.transition.Transition
import com.safframework.statemachine.v2.transition.TransitionDirectionProducerPolicy.DefaultPolicy
import com.safframework.statemachine.v2.utils.extension.findState
import com.safframework.statemachine.v2.utils.extension.machineNotify
import java.util.concurrent.CopyOnWriteArraySet

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.state.BaseStateImpl
 * @author: Tony Shen
 * @date: 2023/7/4 10:41
 * @version: V1.0 <描述当前版本功能>
 */
open class BaseStateImpl(override val name: String?, override val childMode: ChildMode) : InternalState {

    private val _listeners = CopyOnWriteArraySet<Interceptor>()
    override val interceptors: Collection<Interceptor> get() = _listeners

    private val _states = mutableSetOf<InternalState>()
    override val states: Set<IState> get() = _states

    /**
     * In [ChildMode.EXCLUSIVE] might be null only before [setInitialState] call if there are child states.
     */
    protected var currentState: InternalState? = null

    private var _initialState: InternalState? = null
    override val initialState get() = _initialState

    override var parent: InternalState? = null

    override val machine get() = if (this is StateMachine) this else requireParent().machine

    private val _transitions = mutableSetOf<Transition<*>>()
    override val transitions: Set<Transition<*>> get() = _transitions

    private var _isActive = false
    override val isActive get() = _isActive

    private var _isFinished = false
    override val isFinished get() = _isFinished

    override fun <L : Interceptor> addInterceptor(listener: L): L {
        require(_listeners.add(listener)) { "$listener is already added" }
        return listener
    }

    override fun removeInterceptor(listener: Interceptor) {
        _listeners.remove(listener)
    }

    override fun <S : IState> addState(state: S, init: StateBlock<S>?): S {
        check(!machine.isRunning) { "Can not add state after state machine started" }
        if (childMode == ChildMode.PARALLEL)
            require(state !is FinalState) { "Can not add FinalState in parallel child mode" }

        state.name?.let {
            require(findState(it, recursive = false) == null) { "State with name $it already exists" }
        }

        state as InternalState
        require(_states.add(state)) { "$state already added" }
        state.parent = this
        if (init != null)
            state.init()
        return state
    }

    override fun setInitialState(state: IState) {
        require(states.contains(state)) { "$state is not part of $this machine, use addState() first" }
        check(childMode == ChildMode.EXCLUSIVE) { "Can not set initial state in parallel child mode" }
        check(!machine.isRunning) { "Can not change initial state after state machine started" }

        _initialState = state as InternalState
    }

    override fun activeStates(selfIncluding: Boolean): Set<IState> {
        return mutableSetOf<IState>().also { recursiveFillActiveStates(it, this, selfIncluding) }
    }

    override fun <E : Event> addTransition(transition: Transition<E>): Transition<E> {
        _transitions += transition
        return transition
    }

    override fun toString() = "${this::class.simpleName}${if (name != null) "($name)" else ""}"

    override fun toState() = this

    protected open fun onDoEnter(transitionParams: TransitionParams<*>) {
        /* default empty */
    }

    protected open fun onDoExit(transitionParams: TransitionParams<*>) {
        /* default empty */
    }

    override fun doEnter(transitionParams: TransitionParams<*>) {
        if (!_isActive) {
            if (parent != null) machine.log { "Parent $parent entering child $this" }
            _isActive = true
            onDoEnter(transitionParams)
            stateNotify { onEntry(transitionParams) }
        }
    }

    override fun doExit(transitionParams: TransitionParams<*>) {
        if (_isActive) {
            machine.log { "Exiting $this" }
            onDoExit(transitionParams)
            _isActive = false
            stateNotify { onExit(transitionParams) }
        }
    }

    override fun afterChildFinished(finishedChild: InternalState, transitionParams: TransitionParams<*>) {
        if (childMode == ChildMode.PARALLEL && states.all { it.isFinished }) {
            _isFinished = true
            machine.log { "$this finishes" }
            stateNotify { onFinished(transitionParams) }
        }
    }

    override fun <E : Event> recursiveFindUniqueResolvedTransition(event: E): ResolvedTransition<E>? {
        val resolvedTransitions = getCurrentStates()
            .mapNotNull { it.recursiveFindUniqueResolvedTransition(event) }
            .ifEmpty { listOfNotNull(findUniqueResolvedTransition(event)) }
        check(resolvedTransitions.size <= 1) { "Multiple transitions match $event, $transitions in $this" }
        return resolvedTransitions.singleOrNull()
    }

    override fun recursiveEnterInitialStates() {
        if (states.isEmpty()) return

        when (childMode) {
            ChildMode.EXCLUSIVE -> {
                val initialState =
                    checkNotNull(initialState) { "Initial state is not set, call setInitialState() first" }
                setCurrentState(initialState, makeStartTransitionParams(initialState))
                initialState.recursiveEnterInitialStates()
            }
            ChildMode.PARALLEL -> _states.forEach {
                notifyStateEntry(it, makeStartTransitionParams(it))
                it.recursiveEnterInitialStates()
            }
        }
    }

    override fun recursiveEnterStatePath(path: MutableList<InternalState>, transitionParams: TransitionParams<*>) {
        if (path.isEmpty()) {
            recursiveEnterInitialStates()
        } else {
            val state = path.removeLast()
            setCurrentState(state, transitionParams)

            if (state !is StateMachine) // inner state machine manages its internal state by its own
                state.recursiveEnterStatePath(path, transitionParams)
        }
    }

    override fun recursiveExit(transitionParams: TransitionParams<*>) {
        for (currentState in getCurrentStates())
            currentState.recursiveExit(transitionParams)
        doExit(transitionParams)
    }

    override fun recursiveStop() {
        currentState = null
        _isActive = false
        _isFinished = false
        _states.forEach { it.recursiveStop() }
    }

    override fun recursiveFillActiveStates(states: MutableSet<IState>, self: IState, selfIncluding: Boolean) {
        if (!_isActive) return
        if (this == self) {
            if (selfIncluding) states.add(this)
        } else {
            states.add(this)
        }

        for (currentState in getCurrentStates()) {
            // do not include nested state machine states
            if (currentState is StateMachine)
                states.add(currentState)
            else
                currentState.recursiveFillActiveStates(states, self, selfIncluding)
        }
    }

    private fun requireCurrentState() = requireNotNull(currentState) { "Current state is not set" }

    private fun getCurrentStates() = when (childMode) {
        ChildMode.EXCLUSIVE -> listOfNotNull(currentState)
        ChildMode.PARALLEL -> _states.toList()
    }

    private fun setCurrentState(state: InternalState, transitionParams: TransitionParams<*>) {
        require(childMode == ChildMode.EXCLUSIVE) { "Cannot set current state in child mode $childMode" }
        require(states.contains(state)) { "$state is not a child of $this" }

        if (currentState == state) return
        currentState?.recursiveExit(transitionParams)
        currentState = state

        notifyStateEntry(state, transitionParams)
    }

    private fun notifyStateEntry(state: InternalState, transitionParams: TransitionParams<*>) {
        val finish = when (childMode) {
            ChildMode.EXCLUSIVE -> state is IFinalState
            ChildMode.PARALLEL -> states.all { it.isFinished }
        }

        if (finish) {
            _isFinished = true
            machine.log { "$this finishes" }
        }

        state.doEnter(transitionParams)

        val machine = machine as InternalStateMachine
        if (finish) stateNotify { onFinished(transitionParams) }

        machine.machineNotify { onStateChanged(state) }

        if (finish) parent?.afterChildFinished(this, transitionParams)
    }

    internal fun switchToTargetState(
        targetState: InternalState,
        fromState: InternalState,
        transitionParams: TransitionParams<*>
    ) {
        val path = fromState.findPathFromTargetToLca(targetState)
        val lca = path.removeLast()
        lca.recursiveEnterStatePath(path, transitionParams)
    }

    internal fun makeStartTransitionParams(
        sourceState: IState,
        targetState: IState = sourceState
    ): TransitionParams<*> {
        val transition = DefaultTransition(
            "Starting",
            EventMatcher.isInstanceOf<StartEvent>(),
            sourceState,
            targetState,
        )

        return TransitionParams(
            transition,
            transition.produceTargetStateDirection(DefaultPolicy(StartEvent)),
            StartEvent,
        )
    }
}