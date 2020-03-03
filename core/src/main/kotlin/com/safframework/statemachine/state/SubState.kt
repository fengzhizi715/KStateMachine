package com.safframework.statemachine.state

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.SubState
 * @author: Tony Shen
 * @date: 2020-02-28 00:03
 * @version: V1.0 <描述当前版本功能>
 */
class SubState(subStateName: BaseState,vararg states: State):State(subStateName) {

    private val subStateMachine: StateMachine

    init {
        val stateList = states.toMutableList()
        val initialState = stateList.removeAt(0)
        subStateMachine = StateMachine.buildStateMachine(subStateName.toString(),initialState.name) {
            states.forEach { this.addState(it) }
        }
        subStateMachine.initialize()
        subStateMachine.container = this
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