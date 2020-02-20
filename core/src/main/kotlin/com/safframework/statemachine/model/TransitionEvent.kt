package com.safframework.statemachine.model

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.model.TransitionEvent
 * @author: Tony Shen
 * @date: 2020-02-20 16:18
 * @version: V1.0 <描述当前版本功能>
 */
sealed class TransitionEvent {

    /**
     * Event signal when a transition is in progress.
     *
     * @param currentState the current state of the machine
     * @param transition the transition that initiated the state change
     * @param targetState the resulting state of this transition
     */
    data class EnterTransition(
        val stateMachine: StateMachine,
        val currentState: BaseState,
        val transition: Transition,
        val targetState: BaseState
    ) : TransitionEvent()

    /**
     * Event signal when a transition has completed.
     *
     * @param previousState the previous state of the machine before the transition was applied
     * @param transition the transition that initiated the state change
     * @param currentState the resulting state of this transition
     */
    data class ExitTransition(
        val stateMachine: StateMachine,
        val previousState: BaseState,
        val transition: Transition,
        val currentState: BaseState
    ) : TransitionEvent()
}