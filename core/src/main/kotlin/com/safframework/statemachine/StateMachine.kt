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
    private var globalInterceptor:GlobalInterceptor?=null

    /**
     * 初始化状态机，并进入初始化状态
     */
    fun initialize() {
        if(initialized.compareAndSet(false, true)){
            currentState = getState(initialState)
            globalInterceptor?.stateEntered(currentState)
            currentState.enter()
            globalInterceptor?.stateExited(currentState)
        }
    }

    /**
     * 向状态机添加 State
     */
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

    fun interceptor(globalInterceptor:GlobalInterceptor):StateMachine {

        this.globalInterceptor = globalInterceptor
        return this
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
                globalInterceptor?.stateEntered(currentState)
                state.enter()
                globalInterceptor?.stateExited(currentState)

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