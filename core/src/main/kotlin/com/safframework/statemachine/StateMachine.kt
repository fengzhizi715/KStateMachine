package com.safframework.statemachine

import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @FileName:
 *          com.safframework.statemachine.StateMachine
 * @author: Tony Shen
 * @date: 2020-02-14 21:50
 * @version: V1.0 <描述当前版本功能>
 */
class StateMachine private constructor(private val initialState: BaseState) {

    private lateinit var currentState: State    // 当前状态
    private val states = mutableListOf<State>() // 状态列表
    private val initialized = AtomicBoolean(false) // 是否初始化


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
        if(initialized.compareAndSet(false, true)){
            currentState = getState(initialState)
            currentState.enter()
        }
    }

    /**
     * Gives the FSM an event to act upon, state is then changed and actions are performed
     */
    @Synchronized
    fun sendEvent(e: BaseEvent) {
        try {
            val transition = currentState.getTransitionForEvent(e)

            val guard = transition.getGuard()?.invoke()?:true

            if (guard) {
                val state = transition.applyTransition { getState(it) }
                state.enter()

                currentState = state
            } else {
                println("$transition 失败")
            }
        } catch (exc: NoSuchElementException) {
            throw IllegalStateException("This state doesn't support transition on ${e.javaClass.simpleName}")
        }
    }

    @Synchronized
    fun getCurrentState(): BaseState = this.currentState.name

    companion object {

        fun buildStateMachine(initialStateName: BaseState, init: StateMachine.() -> Unit): StateMachine {
            val stateMachine = StateMachine(initialStateName)
            stateMachine.init()
            return stateMachine
        }
    }
}