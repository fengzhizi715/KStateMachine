package com.safframework.statemachine.context

import com.safframework.statemachine.BaseEvent
import com.safframework.statemachine.State
import com.safframework.statemachine.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.context.StateContext
 * @author: Tony Shen
 * @date: 2020-02-19 21:19
 * @version: V1.0 <描述当前版本功能>
 */
interface StateContext {

    fun getEvent(): BaseEvent

    fun getSource(): State

    fun getTarget(): State

    fun getException(): Exception?

    fun setException(exception: Exception)

    fun getTransition(): Transition
}