package com.safframework.statemachine.utils

import com.safframework.statemachine.interceptor.Interceptor
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

internal typealias TransitionDirectionProducer<E> = (TransitionDirectionProducerPolicy<E>) -> TransitionDirection

typealias StateMachineBlock = StateMachine.() -> Unit

typealias InterceptorBlock =  Interceptor.() -> Unit

typealias TransitionAction = (TransitionParams<*>) -> Unit

typealias TransitionActionBlock = TransitionAction.() -> Unit

typealias Guard<E> = (E) -> Boolean