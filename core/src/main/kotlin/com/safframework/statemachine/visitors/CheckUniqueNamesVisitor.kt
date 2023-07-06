package com.safframework.statemachine.visitors

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.visitors.CheckUniqueNamesVisitor
 * @author: Tony Shen
 * @date: 2023/7/4 16:06
 * @version: V1.0 <描述当前版本功能>
 */
internal class CheckUniqueNamesVisitor: Visitor {
    private val stateNames = mutableSetOf<String>()
    private val transitionNames = mutableSetOf<String>()

    override fun visit(machine: StateMachine) {
        machine.name?.let { check(stateNames.add(it)) { "State name is not unique: $it" } }
        machine.visitChildren()
    }

    override fun visit(state: IState) {
        state.name?.let { check(stateNames.add(it)) { "State name is not unique: $it" } }
        if (state !is StateMachine) // do not check nested machines
            state.visitChildren()
    }

    override fun <E : Event> visit(transition: Transition<E>) {
        transition.name?.let { check(transitionNames.add(it)) { "Transition name is not unique: $it" } }
    }

    private fun IState.visitChildren() {
        transitions.forEach { visit(it) }
        states.forEach { visit(it) }
    }
}