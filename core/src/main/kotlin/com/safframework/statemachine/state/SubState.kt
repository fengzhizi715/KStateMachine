package com.safframework.statemachine.state

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState

/**
 * event 交给 SubState 中的 state 处理，
 * 如果无法不处理，状态机会需要把这个 event 交给他的 SuperState 处理。
 *
 * @FileName:
 *          com.safframework.statemachine.state.SubState
 * @author: Tony Shen
 * @date: 2020-02-28 00:03
 * @version: V1.0 <描述当前版本功能>
 */
class SubState(subStateName: BaseState,vararg states: State):State(subStateName) {

    private val subStateMachine: StateMachine
    private val initialState:State

     init {
        val stateList = states.toMutableList()
        initialState = stateList.removeAt(0)
        subStateMachine = StateMachine.buildStateMachine(subStateName.toString(),initialState.name) {
            states.forEach {
                addState(it)
            }
//            initialize()
            container = this@SubState
        }
    }

    override fun processEvent(event: BaseEvent): Boolean = when {
        subStateMachine.sendEvent(event) -> true
        else -> super.processEvent(event)
    }

    override fun addParent(parent: StateMachine) {
        subStateMachine.addParent(parent)
    }

    override fun getDescendantStates(): Set<State> = subStateMachine.descendantStates

    override fun getAllActiveStates(): Collection<State> = subStateMachine.getAllActiveStates()
}