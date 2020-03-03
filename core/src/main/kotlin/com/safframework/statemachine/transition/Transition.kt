package com.safframework.statemachine.transition

import com.safframework.statemachine.Guard
import com.safframework.statemachine.TransitionAction
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState

/**
 * 从一个状态切换到另一个状态
 * @FileName:
 *          com.safframework.statemachine.transition.Transition
 * @author: Tony Shen
 * @date: 2020-02-14 21:40
 * @version: V1.0 <描述当前版本功能>
 */
class Transition(private val event: BaseEvent, private val sourceState: BaseState, private val targetState: BaseState, private val transitionType: TransitionType, private var guard: Guard?= null) {

    private val actions = mutableListOf<TransitionAction>()

    /**
     * 是否转换
     * @param context
     */
    fun transit(context: StateContext): Boolean {
        executeTransitionActions(context)
        return context.getException() == null
    }

    /**
     * 执行 Transition 的 Action
     */
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
     * 添加一个 action，在状态转换时执行
     */
    fun action(action: TransitionAction):Transition {
        actions.add(action)
        return this
    }

    /**
     * 设置检测条件，判断是否满足状态转换的条件，满足则执行状态转换
     */
    fun guard(guard: Guard):Transition {
        this.guard = guard
        return this
    }

    fun getGuard(): Guard? = guard

    fun getSourceState(): BaseState = sourceState

    fun getTargetState(): BaseState = targetState

    fun getTransitionType(): TransitionType = transitionType

    fun getActions(): MutableList<TransitionAction> = actions

    override fun toString(): String = "${sourceState.javaClass.simpleName} transition to ${targetState.javaClass.simpleName} on ${event.javaClass.simpleName}"
}