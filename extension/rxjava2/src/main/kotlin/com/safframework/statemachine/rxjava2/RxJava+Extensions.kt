package com.safframework.statemachine.rxjava2

import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.interceptor.Interceptor
import com.safframework.statemachine.model.TransitionEvent
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
        val rxInterceptor = RxInterceptor(emitter)
        registerInterceptor(rxInterceptor)
        emitter.setCancellable {
            unregisterInterceptor(rxInterceptor)
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

private class RxInterceptor(
    private val emitter: ObservableEmitter<TransitionEvent>
) : Interceptor {

    override fun enteringState(stateMachine: StateMachine, stateContext: StateContext) =
        emitter.onNext(TransitionEvent.EnterTransition(stateMachine, stateContext))

    override fun enteredState(stateMachine: StateMachine, stateContext: StateContext) =
        emitter.onNext(TransitionEvent.ExitTransition(stateMachine, stateContext))
}