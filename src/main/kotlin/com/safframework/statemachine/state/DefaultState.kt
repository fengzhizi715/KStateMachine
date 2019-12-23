package com.safframework.statemachine.state

import com.safframework.statemachine.action.Action
import com.safframework.statemachine.message.Message

/**
 * Created by tony on 2019/12/23.
 */
class DefaultState<S, E>(
    state: S, initial: Boolean, suspend: Boolean, end: Boolean,
    entryActions: MutableCollection<out Action<S, E>>,
    exitActions: MutableCollection<out Action<S, E>>
) : State<S, E> {

    private var state: S
    private var initial = false
    private var suspend = false
    private var end = false
    private val entryActions: MutableCollection<out Action<S, E>>
    private val exitActions: MutableCollection<out Action<S, E>>

    init {
        this.state = state
        this.initial = initial
        this.suspend = suspend
        this.end = end
        this.entryActions = entryActions
        this.exitActions = exitActions
    }

    /**
     * 发送事件的包装类.
     * @param event 事件包装类
     * @return 如果事件被接受返回true 否则false
     */
    override fun sendEvent(event: Message<E>): Boolean {
        return false
    }

    /**
     * 获取状态ID
     */
    override fun getId(): S {
        return state
    }

    override fun isInitial(): Boolean {
        return initial
    }

    override fun isEnd(): Boolean {
        return end
    }

    /**
     * 是否为挂起状态
     */
    override fun isSuspend(): Boolean {
        return suspend
    }

    /**
     * 进入Actions
     * @return actions
     */
    override fun getEntryActions(): MutableCollection<out Action<S, E>> {
        return entryActions
    }

    /**
     * 所有退出的actions
     * @return actions
     */
    override fun getExitActions(): MutableCollection<out Action<S, E>> {
        return exitActions
    }
}