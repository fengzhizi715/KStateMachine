package com.safframework.statemachine

import com.safframework.statemachine.model.BaseState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TransitionCallback
 * @author: Tony Shen
 * @date: 2020-02-20 11:12
 * @version: V1.0 <描述当前版本功能>
 */
interface TransitionCallback {

    /**
     * After a state transition has been verified to be legal but has not yet been applied to the machine.
     *
     * @param stateMachine the machine notifying the state change
     * @param currentState the current state of the machine
     * @param transition the transition that initiated the state change
     * @param targetState the resulting state of this transition
     */
    fun enteringState(
        stateMachine: StateMachine,
        currentState: BaseState,
        transition: Transition,
        targetState: BaseState
    )

    /**
     * After a state transition has been verified to be legal and also applied to a machine.
     *
     * @param stateMachine the machine notifying the state change
     * @param previousState the previous state of the machine before the transition was applied
     * @param transition the transition that initiated the state change
     * @param currentState the resulting state of this transition
     */
    fun enteredState(
        stateMachine: StateMachine,
        previousState: BaseState,
        transition: Transition,
        currentState: BaseState
    )

}