package com.safframework.statemachine.guard

import com.safframework.statemachine.context.StateContext

/**
 * 断言接口，为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态
 *
 * @FileName:
 *          com.safframework.statemachine.guard.Guard
 * @author: Tony Shen
 * @date: 2020-02-12 14:54
 * @version: V1.0 <描述当前版本功能>
 */
interface Guard<S, E> {

    fun evaluate(context: StateContext<S, E>): Boolean
}