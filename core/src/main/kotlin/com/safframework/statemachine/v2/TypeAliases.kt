package com.safframework.statemachine.v2

import com.safframework.statemachine.v2.statemachine.StateMachine
import com.safframework.statemachine.v2.transition.InternalTransition
import com.safframework.statemachine.v2.transition.TransitionDirection
import com.safframework.statemachine.v2.transition.TransitionDirectionProducerPolicy

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.TypeAliases
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
