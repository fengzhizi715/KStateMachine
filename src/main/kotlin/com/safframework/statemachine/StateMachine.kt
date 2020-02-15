package com.safframework.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.StateMachine
 * @author: Tony Shen
 * @date: 2020-02-14 21:50
 * @version: V1.0 <描述当前版本功能>
 */
class StateMachine private constructor(private val initialState: BaseState) {

    private lateinit var currentState: State
    private val states = mutableListOf<State>()

    fun state(stateName: BaseState, init: State.() -> Unit):StateMachine {
        val state = State(stateName)
        state.init()

        states.add(state)
        return this
    }

    /**
     * Translates state name to an object
     */
    private fun getState(stateType: BaseState): State = states.firstOrNull { stateType.javaClass == it.name.javaClass } ?: throw NoSuchElementException(stateType.javaClass.canonicalName)

    /**
     * Initializes the [StateMachine] and puts it on the first state
     */
    fun initialize() {
        currentState = getState(initialState)
        currentState.enter()
    }

    /**
     * Gives the FSM an event to act upon, state is then changed and actions are performed
     */
    fun sendEvent(e: BaseEvent) {
        try {
            val transition = currentState.getTransitionForEvent(e)

            val guard = transition.guard?.invoke()?:true

            if (guard) {
                // Indirectly get the state stored in edge
                // The syntax is weird to guarantee that the states are changed
                // once the actions are performed
                // This line just queries the next state name (Class) from the
                // state list and retrieves the corresponding state object.
                val state = transition.applyTransition { getState(it) }
                state.enter()

                currentState = state
            } else {
                println("$transition 跳转到下一个状态失败")
            }
        } catch (exc: NoSuchElementException) {
            throw IllegalStateException("This state doesn't support " +
                    "transition on ${e.javaClass.simpleName}")
        }
    }

    fun getCurrentState(): BaseState {
        return this.currentState.name
    }

    companion object {
        fun buildStateMachine(initialStateName: BaseState, init: StateMachine.() -> Unit): StateMachine {
            val stateMachine = StateMachine(initialStateName)
            stateMachine.init()
            return stateMachine
        }
    }
}