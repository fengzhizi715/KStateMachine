package com.safframework.statemachine.context

import com.safframework.statemachine.message.Message
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition

/**
 * 状态上下文
 * Created by tony on 2019/12/21.
 */
interface StateContext<S, E> {

    fun getMessage(): Message<E>

    fun getEvent(): E

    fun getSource(): State<S, E>

    fun getTarget(): State<S, E>

    fun getException(): Exception?

    fun setException(exception: Exception)

    fun getTransition(): Transition<S, E>
}