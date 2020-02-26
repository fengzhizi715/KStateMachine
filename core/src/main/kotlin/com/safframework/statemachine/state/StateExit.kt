package com.safframework.statemachine.state

import com.safframework.statemachine.StateAction

/**
 *
 * @FileName:
 *          com.safframework.statemachine.state.StateExit
 * @author: Tony Shen
 * @date: 2020-02-26 23:57
 * @version: V1.0 <描述当前版本功能>
 */
class StateExit {

    private val actions = mutableListOf<StateAction>()

    /**
     * 添加一个 action，在状态转换时执行(时间点是在状态转换之前)
     */
    fun action(action: StateAction) {
        actions.add(action)
    }

    fun getActions() = actions
}