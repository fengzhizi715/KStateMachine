package com.safframework.statemachine.transition

import com.safframework.statemachine.action.Action
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.guard.Guard
import com.safframework.statemachine.state.State

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.AbstractTransition
 * @author: Tony Shen
 * @date: 2020-02-13 15:07
 * @version: V1.0 <描述当前版本功能>
 */
open abstract class AbstractTransition<S, E>(
    private val source: State<S, E>,
    private val target: State<S, E>,
    private val  event: E,
    private val guard: Guard<S, E>,
    private val actions: MutableCollection<Action<S, E>>
) : Transition<S, E> {

    override fun getSource(): State<S, E> = source

    override fun getTarget(): State<S, E> = target

    override fun transit(context: StateContext<S, E>): Boolean {
        executeTransitionActions(context)
        return context.getException() == null
    }

    override fun executeTransitionActions(context: StateContext<S, E>) {
        if (actions == null) {
            return
        }
        for (action in actions) {
            try {
                action.execute(context)
            } catch (e: Exception) {
                context.setException(e)
                println("Action执行结束，发生异常 Source-->${context.getSource().getId()} ,Target-->${context.getTarget().getId()} ,Event->${context.getEvent()}")
                return
            }
        }
    }

    override fun guard(): Guard<S, E> = guard

    override fun getEvent(): E = event

    override fun getActions(): Collection<Action<S, E>> = actions

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractTransition<*, *>

        if (source != other.source) return false
        if (target != other.target) return false
        if (event != other.event) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + (event?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "AbstractTransition [source=" + getSource() + ", target=" + target + "]"
}