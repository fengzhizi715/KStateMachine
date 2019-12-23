package com.safframework.statemachine.message

/**
 * Created by tony on 2019/12/22.
 */
class DefaultMessage<T>(payload: T, headers: MessageHeaders) : Message<T> {

    private val payload: T
    private val headers: MessageHeaders

    init {
        this.payload = payload
        this.headers = headers
    }

    override fun getPayload(): T = payload

    override fun getHeaders(): MessageHeaders = headers
}