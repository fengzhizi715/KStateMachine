package com.safframework.statemachine

/**
 * 构成状态机的基本单位，状态机在任何特定时间都可处于某一状态。
 * @FileName:
 *          com.safframework.statemachine.State
 * @author: Tony Shen
 * @date: 2020-02-14 21:42
 * @version: V1.0 <描述当前版本功能>
 */
class State(val name: BaseState) {

    private val transitions = hashMapOf<BaseEvent, Transition>()   // Convert to HashMap with event as key
    private val stateActions = mutableListOf<(State) -> Unit>()

    /**
     * Creates a transition from a [State] to another when a [BaseEvent] occurs
     * @param event: 触发事件
     * @param targetState: 下一个状态
     * @param guard: 断言接口，为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态
     * @param init
     */
    fun transition(event: BaseEvent, targetState: BaseState, guard: (()->Boolean)?=null, init: Transition.() -> Unit):State {
        val transition = Transition(event, targetState, guard)
        transition.init()

        if (transitions.containsKey(event)) {
            throw StateMachineException("Adding multiple transitions for the same event is invalid")
        }

        transitions[event] = transition
        return this
    }

    /**
     * state 执行的 action
     */
    fun action(action: (State) -> Unit) {
        stateActions.add(action)
    }

    /**
     * 进入 state 并执行所有的 action
     */
    fun enter() {
        stateActions.forEach {
            it(this)
        }
    }

    /**
     * 获取 Transition
     */
    fun getTransitionForEvent(event: BaseEvent): Transition = transitions[event]?:throw IllegalStateException("Event $event isn't registered with state ${this.name}")

    override fun toString(): String = name.javaClass.simpleName
}
