package com.safframework.statemachine

import com.safframework.statemachine.interceptor.Interceptor
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.model.TransitionEvent
import com.safframework.statemachine.transition.Transition
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
        registerInterceptor(rxCallback)
        emitter.setCancellable {
            unregisterInterceptor(rxCallback)
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

private class RxStateCallback(
    private val emitter: ObservableEmitter<TransitionEvent>
) : Interceptor {

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