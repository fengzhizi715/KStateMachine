package com.safframework.statemachine

/**
 * 从一个状态切换到另一个状态
 * @FileName:
 *          com.safframework.statemachine.Transition
 * @author: Tony Shen
 * @date: 2020-02-14 21:40
 * @version: V1.0 <描述当前版本功能>
 */
class Transition(private val event: BaseEvent, private val targetState: BaseState) {

    private val actions = mutableListOf<(Transition) -> Unit>()

    /**
     * Add an action to be performed upon transition
     */
    fun action(action: (Transition) -> Unit) {
        actions.add(action)
    }

    /**
     * Apply the transition actions
     */
    fun applyTransition(getNextState: (BaseState) -> State): State {
        actions.forEach {
            it(this)
        }

        return getNextState(targetState)
    }

    override fun toString(): String {
        return "Transition to ${targetState.javaClass.simpleName} on ${event.javaClass.simpleName}"
    }
}