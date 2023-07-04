package com.safframework.statemachine.v2.statemachine

import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.state.InternalState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.statemachine.InternalStateMachine
 * @author: Tony Shen
 * @date: 2023/7/4 14:50
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalStateMachine : StateMachine, InternalState {
    fun startFrom(state: IState)
}