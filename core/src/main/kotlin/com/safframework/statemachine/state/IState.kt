package com.safframework.statemachine.state

import com.safframework.statemachine.utils.StateBlock
import com.safframework.statemachine.StateTransitionsHelper
import com.safframework.statemachine.interceptor.StateInterceptor
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.StateMachineDslMarker
import com.safframework.statemachine.visitors.Visitor
import com.safframework.statemachine.visitors.VisitorAcceptor

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.IState
 * @author: Tony Shen
 * @date: 2023/7/4 09:52
 * @version: V1.0 <描述当前版本功能>
 */
enum class ChildMode { EXCLUSIVE, PARALLEL }

@StateMachineDslMarker
interface IState : StateTransitionsHelper, VisitorAcceptor {
    val name: String?
    val states: Set<IState>
    val initialState: IState?
    val parent: IState?
    val machine: StateMachine
    val isActive: Boolean
    val isFinished: Boolean
    val interceptors: Collection<StateInterceptor>
    val childMode: ChildMode

    fun <I : StateInterceptor> addInterceptor(interceptor: I): I
    fun removeInterceptor(interceptor: StateInterceptor)

    fun <S : IState> addState(state: S, init: StateBlock<S>? = null): S

    /**
     * Currently initial state is mandatory, but if we add parallel states it might change.
     */
    fun setInitialState(state: IState)

    /**
     * Set of states that the state is currently in. Including state itself if [selfIncluding] is true.
     * Internal states of nested machines are not included.
     */
    fun activeStates(selfIncluding: Boolean = false): Set<IState>

    override fun accept(visitor: Visitor) = visitor.visit(this)
}