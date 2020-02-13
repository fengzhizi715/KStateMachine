package com.safframework.statemachine.message

/**
 * Created by tony on 2019/12/22.
 */
class DefaultMessage<T>(private val payload: T, private var headers: MessageHeaders?=null) : Message<T> {

    override fun getPayload(): T = payload

    override fun getHeaders(): MessageHeaders? = headers
}