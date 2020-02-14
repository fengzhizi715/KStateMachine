package com.safframework.statemachine.message

/**
 * T 为状态机的事件(又称为条件)。当一个事件/条件被满足时，可能将会触发一个动作，或者执行一次状态的迁移
 *
 * Created by tony on 2019/12/21.
 */
interface Message<T> {

    fun getPayload(): T

    fun getHeaders(): MessageHeaders?
}