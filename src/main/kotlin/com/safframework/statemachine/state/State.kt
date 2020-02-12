package com.safframework.statemachine.state

import com.safframework.statemachine.action.Action
import com.safframework.statemachine.message.Message

/**
 * 构成状态机的基本单位，状态机在任何特定时间都可处于某一状态。
 * Created by tony on 2019/12/21.
 */
interface State<S, E> {

    /**
     * 发送事件的包装类.
     * @param event 事件包装类
     * @return 如果事件被接受返回true 否则false
     */
    fun sendEvent(event: Message<E>): Boolean

    /**
     * 获取状态ID
     */
    fun getId(): S

    /**
     * 是否初始化状态
     */
    fun isInitial(): Boolean

    /**
     * 是否结束状态
     */
    fun isEnd(): Boolean

    /**
     * 是否为挂起状态
     */
    fun isSuspend(): Boolean

    /**
     * 进入 State 的所有 Actions
     */
    fun getEntryActions(): Collection<Action<S, E>>

    /**
     * 退出 State 的所有 Actions
     * @return actions
     */
    fun getExitActions(): Collection<Action<S, E>>
}