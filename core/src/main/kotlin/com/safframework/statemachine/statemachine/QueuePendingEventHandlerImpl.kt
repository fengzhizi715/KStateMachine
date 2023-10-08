package com.safframework.statemachine.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.statemachine.QueuePendingEventHandlerImpl
 * @author: Tony Shen
 * @date: 2023/10/8 12:10
 * @version: V1.0 <描述当前版本功能>
 */
interface QueuePendingEventHandler: StateMachine.PendingEventHandler {

    fun checkEmpty()

    fun nextEventAndArgument(): EventAndArgument<*>?

    fun clear()
}

internal class QueuePendingEventHandlerImpl(private val machine: StateMachine): QueuePendingEventHandler {
    private val queue = ArrayDeque<EventAndArgument<*>>()

    override fun checkEmpty() = check(queue.isEmpty()) { "Event queue is not empty, internal error" }

    override fun onPendingEvent(eventAndArgument: EventAndArgument<*>) {
        machine.log {
            "$machine queued event ${eventAndArgument.event::class.simpleName} with argument ${eventAndArgument.argument}"
        }
        queue.add(eventAndArgument)
    }

    override fun nextEventAndArgument() = queue.removeFirstOrNull()

    override fun clear() = queue.clear()
}