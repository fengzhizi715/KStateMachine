package com.safframework.statemachine.state

import com.safframework.statemachine.Guard
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.transition.TransitionType

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.IState
 * @author: Tony Shen
 * @date: 2020-02-26 11:51
 * @version: V1.0 <描述当前版本功能>
 */
interface IState {

    fun enter()

    fun exit()

    fun transition(event: BaseEvent, targetState: BaseState, transitionType: TransitionType = TransitionType.External, guard: Guard?=null, init: Transition.() -> Unit): IState
}
