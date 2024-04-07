package com.safframework.statemachine.statemachine

import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.exception.StateMachineException
import java.lang.Exception

/**
 *
 * @FileName:
 *          com.safframework.statemachine.statemachine.InternalHandlers
 * @author: Tony Shen
 * @date:  2024/4/5 13:13
 * @version: V1.0 <描述当前版本功能>
 */
internal object NullLogger : StateMachine.Logger {
    override fun log(message: String) {}
}

internal class DefaultIgnoredEventHandlerImpl(private val machine: StateMachine) : StateMachine.IgnoredEventHandler  {

    override fun onIgnoredEvent(eventAndArgument: EventAndArgument<*>) {
        val event = eventAndArgument.event
        if (event is DataEvent<*>) {
            machine.log { "${this.javaClass.simpleName} ignored ${event::class.simpleName}(${event.data})" }
        } else {
            machine.log { "${this.javaClass.simpleName} ignored ${event::class.simpleName}" }
        }
    }
}

internal object DefaultExceptionListener : StateMachine.ExceptionListener {
    override fun onException(exception: Exception) {
        throw StateMachineException(exception.message?:"")
    }
}