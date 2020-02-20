package com.safframework.statemachine

import com.safframework.statemachine.model.BaseState
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 *
 * @FileName:
 *          com.safframework.statemachine.`RxJava+Extensions`
 * @author: Tony Shen
 * @date: 2020-02-15 17:30
 * @version: V1.0 <描述当前版本功能>
 */

val StateMachine.stateObservable: Observable<TransitionEvent>
    get() = Observable.create { emitter ->
        val rxCallback = RxStateCallback(emitter)
        registerCallback(rxCallback)
        emitter.setCancellable {
            unregisterCallback(rxCallback)
        }
    }

val StateMachine.enterTransitionObservable: Observable<TransitionEvent.EnterTransition>
    get() = stateObservable
        .filter { event -> event is TransitionEvent.EnterTransition }
        .map { event -> event as TransitionEvent.EnterTransition }

val StateMachine.exitTransitionObservable: Observable<TransitionEvent.ExitTransition>
    get() = stateObservable
        .filter { event -> event is TransitionEvent.ExitTransition }
        .map { event -> event as TransitionEvent.ExitTransition }

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

private class RxStateCallback(
    private val emitter: ObservableEmitter<TransitionEvent>
) : TransitionCallback {

    override fun enteringState(
        stateMachine: StateMachine,
        currentState: BaseState,
        transition: Transition,
        targetState: BaseState
    ) = emitter.onNext(TransitionEvent.EnterTransition(stateMachine, currentState, transition, targetState))

    override fun enteredState(
        stateMachine: StateMachine,
        previousState: BaseState,
        transition: Transition,
        currentState: BaseState
    ) = emitter.onNext(TransitionEvent.ExitTransition(stateMachine, previousState, transition, currentState))

}