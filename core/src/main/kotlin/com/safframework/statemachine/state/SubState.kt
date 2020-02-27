package com.safframework.statemachine.state

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.model.BaseState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.SubState
 * @author: Tony Shen
 * @date: 2020-02-28 00:03
 * @version: V1.0 <描述当前版本功能>
 */
class SubState(val subStateName: BaseState,val stateMachine: StateMachine):State(subStateName) {



}