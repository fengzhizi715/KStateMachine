package com.safframework.statemachine.transition

import com.safframework.statemachine.action.Action
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.state.State

/**
 * 两个状态之间的定向转换关系，状态机对发生的特定类型事件响应后当前状态由A转换到B。
 * Created by tony on 2019/12/21.
 */
interface Transition<S, E> {

    /**
     * 是否转换
     * @param context
     * @return
     */
    fun transit(context: StateContext<S, E>): Boolean

    fun executeTransitionActions(context: StateContext<S, E>)

    fun getActions(): Collection<Action<S, E>>

    fun getSource(): State<S, E>

    fun getTarget(): State<S, E>

    fun getEvent(): E

    companion object {
        const val SUCCESS = "SUCCESS"
        const val FAILED = "FAILED"
    }
}