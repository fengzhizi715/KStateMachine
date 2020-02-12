package com.safframework.statemachine.context

import com.safframework.statemachine.message.Message
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.context.DefaultStateContext
 * @author: Tony Shen
 * @date: 2020-02-12 22:00
 * @version: V1.0 <描述当前版本功能>
 */
class DefaultStateContext<S, E> (private var message: Message<E>,
                                          private var transition: Transition<S, E>,
                                          private var source: State<S, E>?=null,
                                          private var target: State<S, E>,
                                          private var exception: Exception?=null): StateContext<S, E> {

    override fun getMessage(): Message<E> = message

    override fun getEvent(): E = message?.getPayload()

    override fun getSource(): State<S, E> = source ?: transition?.getSource()

    override fun getTarget(): State<S, E> = target

    override fun getException(): Exception?  = exception

    override fun setException(exception: Exception) {
        this.exception = exception
    }

    override fun getTransition(): Transition<S, E> = transition

    override fun toString(): String = ("DefaultStateContext [ message=" + message +
            ", transition=" + transition + ", source=" + source + ", target="
            + target + ", exception=" + exception + "]")
}