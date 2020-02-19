package com.safframework.statemachine.context

import com.safframework.statemachine.BaseEvent
import com.safframework.statemachine.State
import com.safframework.statemachine.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.context.DefaultContext
 * @author: Tony Shen
 * @date: 2020-02-19 21:22
 * @version: V1.0 <描述当前版本功能>
 */
class DefaultStateContext(private val event: BaseEvent,
                          private val transition: Transition,
                          private val source:State,
                          private val target: State,
                          private var e: Exception?=null ) :StateContext {

    override fun getEvent(): BaseEvent = event

    override fun getSource(): State = source

    override fun getTarget(): State = target

    override fun getException(): Exception? = e

    override fun setException(exception: Exception) {
        this.e = exception
    }

    override fun getTransition(): Transition = transition
}