package com.safframework.statemachine

import com.safframework.statemachine.context.StateContext

/**
 * 从一个状态切换到另一个状态
 * @FileName:
 *          com.safframework.statemachine.Transition
 * @author: Tony Shen
 * @date: 2020-02-14 21:40
 * @version: V1.0 <描述当前版本功能>
 */
class Transition(private val event: BaseEvent, private val sourceState: BaseState,private val targetState: BaseState, private var guard:Guard?= null) {

    private val actions = mutableListOf<TransitionAction>()

    /**
     * 是否转换
     * @param context
     */
    fun transit(context: StateContext): Boolean {
        executeTransitionActions(context)
        return context.getException() == null
    }

    private fun executeTransitionActions(context: StateContext) {

        actions.forEach {
            try {
                it.invoke(this)
            } catch (e:Exception) {
                context.setException(e)
                return
            }
        }
    }

    /**
     * Add an action to be performed upon transition
     */
    fun action(action: TransitionAction) {
        actions.add(action)
    }

    /**
     * Apply the transition actions
     */
    fun applyTransition(getNextState: (BaseState) -> State): State {

        return getNextState(targetState)
    }

    fun guard(guard: Guard) {
        this.guard = guard
    }

    fun getGuard():Guard? = guard

    fun getSourceState():BaseState = sourceState

    fun getTargetState():BaseState = targetState

    override fun toString(): String = "${sourceState.javaClass.simpleName} transition to ${targetState.javaClass.simpleName} on ${event.javaClass.simpleName}"
}