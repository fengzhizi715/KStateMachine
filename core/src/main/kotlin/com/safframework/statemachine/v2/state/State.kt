package com.safframework.statemachine.v2.state

import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.state.State
 * @author: Tony Shen
 * @date: 2023/7/4 10:15
 * @version: V1.0 <描述当前版本功能>
 */
interface State : IState

/**
 * State which holds data while it is active
 */
interface DataState<out D> : IState {
    /**
     * This property might be accessed only while this state is active
     */
    val data: D
}

interface IFinalState : IState {
    override fun <E : Event> addTransition(transition: Transition<E>) =
        throw UnsupportedOperationException("FinalState can not have transitions")
}

interface FinalState : IFinalState, State

interface FinalDataState<out D> : IFinalState, DataState<D>