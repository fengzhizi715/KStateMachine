package com.safframework.statemachine.state

import com.safframework.statemachine.Guard
import com.safframework.statemachine.StateMachine
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.transition.TransitionType

/**
 * 构成状态机的基本单位，状态机在任何特定时间都可处于某一状态。
 * @FileName:
 *          com.safframework.statemachine.state.State
 * @author: Tony Shen
 * @date: 2020-02-14 21:42
 * @version: V1.0 <描述当前版本功能>
 */
open class State(val name: BaseState): IState {

    private val transitions = hashMapOf<BaseEvent, Transition>() // 存储当前 State 相关的所有 Transition
    private var entry:StateEntry?=null
    private var exit:StateExit?=null
    var owner: StateMachine? = null

    /**
     * 向 State 添加 Transition
     * 当一个 Event 被状态机系统分发的时候，状态机用 Action 来进行响应
     * 状态转换可以使用 F(S, E) -> (A, S’) 表示
     *
     * @param event: 触发事件
     * @param targetState: 下一个状态
     * @param guard: 断言接口，为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态
     * @param init
     */
    override fun transition(event: BaseEvent, targetState: BaseState, transitionType: TransitionType, guard: Guard?, init: Transition.() -> Unit):State {

        val transition = Transition(
            event,
            this.name,
            targetState,
            transitionType,
            guard
        ).apply(init)

        if (transitions.containsKey(event)) { // 同一个 Event 不能对应多个 Transition，即 State 只能通过一个 Event 然后 Transition 到另一个 State
            throw StateMachineException("Adding multiple transitions for the same event is invalid")
        }

        transitions[event] = transition
        return this
    }

    /**
     * 进入 State 时，添加 Action
     */
    fun entry(block:StateEntry.() -> Unit) {
        entry = entry?.apply {
            block()
        }?: run{
            StateEntry().apply(block)
        }
    }

    /**
     * 退出 State 时，添加 Action
     */
    fun exit(block:StateExit.() -> Unit) {
        exit = exit?.apply {
            block()
        }?: run{
            StateExit().apply(block)
        }
    }

    /**
     * 进入 State 并执行所有的 Action
     */
    override fun enter() {
        entry?.let {
            it.getActions().forEach{ action -> action.invoke(this) }
        }
    }

    /**
     * 退出 State 并执行所有的 Action
     */
    override fun exit() {
        exit?.let {
            it.getActions().forEach{ action -> action.invoke(this) }
        }
    }

    /**
     * 通过 Event 查找 State 储存的 Transition
     */
    private fun getTransitionForEvent(event: BaseEvent): Transition = transitions[event]?:throw IllegalStateException("Event $event isn't registered with state ${this.name}")

    internal open fun processEvent(event: BaseEvent): Boolean {

        getTransitionForEvent(event).takeIf { owner!=null }?.let {
            owner!!.executeTransition(it, event)
            return true
        }?:return false
    }

    internal open fun addParent(parent: StateMachine) = Unit

    internal open fun getDescendantStates(): Set<State> = setOf()

    internal open fun getAllActiveStates(): Collection<State> = setOf()

    override fun toString(): String = "state: ${name.javaClass.simpleName},owner: ${owner?.name}"
}
