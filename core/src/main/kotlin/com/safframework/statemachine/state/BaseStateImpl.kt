package com.safframework.statemachine.state

import com.safframework.statemachine.utils.ResolvedTransition
import com.safframework.statemachine.utils.StateBlock
import com.safframework.statemachine.algorithm.TreeAlgorithm.findPathFromTargetToLca
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.domain.StartEvent
import com.safframework.statemachine.interceptor.StateInterceptor
import com.safframework.statemachine.statemachine.InternalStateMachine
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.*
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy.DefaultPolicy
import com.safframework.statemachine.utils.extension.findState
import com.safframework.statemachine.utils.extension.machineNotify
import java.util.concurrent.CopyOnWriteArraySet

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.BaseStateImpl
 * @author: Tony Shen
 * @date: 2023/7/4 10:41
 * @version: V1.0 <描述当前版本功能>
 */
open class BaseStateImpl(override val name: String?, override val childMode: ChildMode) : InternalState {

    private val _interceptors = CopyOnWriteArraySet<StateInterceptor>()
    override val interceptors: Collection<StateInterceptor> get() = _interceptors

    private val _states = mutableSetOf<InternalState>()
    override val states: Set<IState> get() = _states

    /**
     * In [ChildMode.EXCLUSIVE] might be null only before [setInitialState] call if there are child states.
     */
    protected var currentState: InternalState? = null

    private var _initialState: InternalState? = null
    override val initialState get() = _initialState

    override var internalParent: InternalState? = null

    override fun setParent(parent: InternalState) {
        check(parent !== internalParent) { "$parent is already a parent of $this" }
        internalParent = parent
    }

    override val machine get() = if (this is StateMachine) this else requireInternalParent().machine

    private val _transitions = mutableSetOf<Transition<*>>()
    override val transitions: Set<Transition<*>> get() = _transitions

    private var _isActive = false
    override val isActive get() = _isActive

    private var _isFinished = false
    override val isFinished get() = _isFinished

    override fun <I : StateInterceptor> addInterceptor(interceptor: I): I {
        require(_interceptors.add(interceptor)) { "$interceptor is already added" }
        return interceptor
    }

    override fun removeInterceptor(interceptor: StateInterceptor) {
        _interceptors.remove(interceptor)
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
        state.setParent(this)
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

    override fun toString() = "${this::class.simpleName}${if (name != null) "[$name]" else ""}"

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
            stateNotify {
                onEntry(transitionParams)
            }
        }
    }

    override fun doExit(transitionParams: TransitionParams<*>) {
        if (_isActive) {
            machine.log { "Exiting $this" }
            onDoExit(transitionParams)
            _isActive = false
            stateNotify {
                onExit(transitionParams)
            }
        }
    }

    override fun afterChildFinished(finishedChild: InternalState, transitionParams: TransitionParams<*>) {
        if (childMode == ChildMode.PARALLEL && states.all { it.isFinished }) {
            _isFinished = true
            machine.log { "$this finishes" }
            stateNotify {
                onFinished(transitionParams)
            }
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
                val initialState = checkNotNull(initialState) { "Initial state is not set, call setInitialState() first" }
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
        ChildMode.PARALLEL  -> _states.toList()
    }

    private fun setCurrentState(state: InternalState, transitionParams: TransitionParams<*>) {
        require(childMode == ChildMode.EXCLUSIVE) { "Cannot set current state in child mode $childMode" }
        require(states.contains(state)) { "$state is not a child of $this" }

        if (currentState == state && transitionParams.transition.type != TransitionType.EXTERNAL) return
        currentState?.recursiveExit(transitionParams)
        currentState = state

        notifyStateEntry(state, transitionParams)
    }

    private fun notifyStateEntry(state: InternalState, transitionParams: TransitionParams<*>) {
        val finish = when (childMode) {
            ChildMode.EXCLUSIVE -> state is IFinalState
            ChildMode.PARALLEL  -> states.all { it.isFinished }
        }

        if (finish) {
            _isFinished = true
            machine.log { "$this finishes" }
        }

        state.doEnter(transitionParams)

        val machine = machine as InternalStateMachine
        if (finish)
            stateNotify {
                onFinished(transitionParams)
            }

        machine.machineNotify {
            onStateChanged(state)
        }

        if (finish)
            internalParent?.afterChildFinished(this, transitionParams)
    }

    internal fun switchToTargetState(
        targetState: InternalState,
        fromState: InternalState,
        transitionParams: TransitionParams<*>
    ) {
        val path = fromState.findPathFromTargetToLca(targetState)
        if (transitionParams.transition.type == TransitionType.EXTERNAL)
            path.last().internalParent?.let { path.add(it) }
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
            TransitionType.LOCAL,
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