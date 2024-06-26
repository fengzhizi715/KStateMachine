package com.safframework.statemachine.utils

import com.safframework.statemachine.interceptor.StateInterceptor
import com.safframework.statemachine.interceptor.TransitionInterceptor
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.InternalTransition
import com.safframework.statemachine.transition.TransitionDirection
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy
import com.safframework.statemachine.transition.TransitionParams

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TypeAliases
 * @author: Tony Shen
 * @date: 2023/7/3 20:41
 * @version: V1.0 <描述当前版本功能>
 */
typealias StateBlock<S> = S.() -> Unit

/**
 * Transition that matches event and has a meaningful direction (except [NoTransition])
 */
typealias ResolvedTransition<E> = Pair<InternalTransition<E>, TransitionDirection>

typealias TransitionDirectionProducer<E> = (TransitionDirectionProducerPolicy<E>) -> TransitionDirection

typealias StateMachineBlock = StateMachine.() -> Unit

typealias StateInterceptorBlock =  StateInterceptor.() -> Unit

typealias TransitionInterceptorBlock = TransitionInterceptor.() -> Unit

typealias TransitionParamsBlock<E> = (TransitionParams<E>) -> Unit

typealias Guard<E> = (E) -> Boolean