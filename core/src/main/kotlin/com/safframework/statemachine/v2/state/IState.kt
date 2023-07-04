package com.safframework.statemachine.v2.state

import com.safframework.statemachine.v2.StateBlock
import com.safframework.statemachine.v2.StateTransitionsHelper
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.statemachine.StateMachine
import com.safframework.statemachine.v2.statemachine.StateMachineDslMarker
import com.safframework.statemachine.v2.visitors.Visitor
import com.safframework.statemachine.v2.visitors.VisitorAcceptor

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.state.IState
 * @author: Tony Shen
 * @date: 2023/7/4 09:52
 * @version: V1.0 <描述当前版本功能>
 */
@StateMachineDslMarker
interface IState : StateTransitionsHelper, VisitorAcceptor {
    val name: String?
    val states: Set<IState>
    val initialState: IState?
    val parent: IState?
    val machine: StateMachine
    val isActive: Boolean
    val isFinished: Boolean
    val listeners: Collection<Listener>
    val childMode: ChildMode

    fun <L : Listener> addListener(listener: L): L
    fun removeListener(listener: Listener)

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

    interface Listener {
        fun onEntry(transitionParams: TransitionParams<*>) = Unit
        fun onExit(transitionParams: TransitionParams<*>) = Unit

        /**
         * If child mode is [ChildMode.EXCLUSIVE] notifies that child [IFinalState] is entered.
         * If child mode is [ChildMode.PARALLEL] notifies that all children has finished.
         */
        fun onFinished(transitionParams: TransitionParams<*>) = Unit
    }
}

enum class ChildMode { EXCLUSIVE, PARALLEL }
