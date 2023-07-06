package com.safframework.statemachine.visitors

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.visitors.Visitor
 * @author: Tony Shen
 * @date: 2023/7/3 20:39
 * @version: V1.0 <描述当前版本功能>
 */
interface Visitor {

    fun visit(machine: StateMachine)

    fun visit(state: IState)

    fun <E : Event> visit(transition: Transition<E>)
}

interface VisitorAcceptor {

    fun accept(visitor: Visitor)
}
