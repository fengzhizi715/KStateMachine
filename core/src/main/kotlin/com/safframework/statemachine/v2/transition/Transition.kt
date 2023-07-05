package com.safframework.statemachine.v2.transition

import com.safframework.statemachine.v2.TransitionAction
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.visitors.Visitor
import com.safframework.statemachine.v2.visitors.VisitorAcceptor

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.transition.Transition
 * @author: Tony Shen
 * @date: 2023/7/3 20:42
 * @version: V1.0 <描述当前版本功能>
 */
interface Transition<E : Event> : VisitorAcceptor {
    val name: String?
    val eventMatcher: EventMatcher<E>
    val sourceState: IState

    /**
     * This parameter may be used to pass arbitrary data with a transition to targetState.
     * This argument must be set from transition listener. Such transition must have only one listener
     * that sets the argument.
     */
    var argument: Any?
    val actions: Collection<TransitionAction>

    fun <A : TransitionAction> addAction(action: A): A
    fun removeAction(action: TransitionAction)

    /**
     * Checks if the [event] matches this [Transition]
     */
    fun isMatchingEvent(event: Event): Boolean

    override fun accept(visitor: Visitor) = visitor.visit(this)
}