package com.safframework.statemachine

import com.safframework.statemachine.message.Message
import com.safframework.statemachine.message.MessageHeaders
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition

/**
 * Created by tony on 2020/1/2.
 */
interface StateMachine<S, E> {

    /**
     * 初始化状态
     */
    fun getInitialState(): State<S, E>

    /**
     * 开始执行状态机，并自动事件驱动所有状态扭转，直到有事件不被接受或事件中发生异常
     * @param headers 传入参数，可在触发事件中使用
     */
    fun start(headers: MessageHeaders?=null): Boolean

    /**
     * 触发事件
     * @param event Message<E>
     * @return 状态机是否接受事件
     */
    fun sendEvent(event: Message<E>): Boolean

    /**
     * 触发事件
     * @param event E
     * @return 状态机是否接受事件
     */
    fun sendEvent(event: E): Boolean

    /**
     * 获取状态机当前状态
     * @return S
     */
    fun getState(): State<S, E>

    /**
     * 获取当前事件
     */
    fun getEvent(): Message<E>

    /**
     * 重置状态机当前状态
     * @param newState S
     */
    fun resetStateMachine(newState: S)

    /**
     * 获取状态机所有状态集合
     */
    fun getStates(): Collection<State<S, E>>

    /**
     * 获取状态机所有转换器
     */
    fun getTransitions(): Map<S, Collection<Transition<S, E>>>

    /**
     * 当前转换器
     */
    fun transition(): Transition<S, E>

    /**
     * 状态机是否完成，如果有异常不接受事件或扭转到 end 状态 都是完成
     */
    fun isComplete(): Boolean

    /**
     * 设置状态机异常
     */
    fun setStateMachineError(exception: Exception)

    /**
     * 状态机是否有异常
     */
    fun getStateMachineError(): Exception
}