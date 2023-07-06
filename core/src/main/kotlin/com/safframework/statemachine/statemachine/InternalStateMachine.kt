package com.safframework.statemachine.statemachine

import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.InternalState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.statemachine.InternalStateMachine
 * @author: Tony Shen
 * @date: 2023/7/4 14:50
 * @version: V1.0 <描述当前版本功能>
 */
interface InternalStateMachine : StateMachine, InternalState {
    fun startFrom(state: IState)
}