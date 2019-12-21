package com.safframework.statemachine.message

/**
 * Created by tony on 2019/12/21.
 */
interface Message<T> {

    fun getPayload(): T

    fun getHeaders(): MessageHeaders
}