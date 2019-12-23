package com.safframework.statemachine.message

import java.util.*


/**
 * Created by tony on 2019/12/21.
 */
class MessageHeaders {
    private var headers: MutableMap<String, Any>

    init {
        headers = HashMap()
    }

    fun getHeaders(): Map<String, Any> = headers

    fun getHeader(key: String?): Any? = headers[key]

    fun setHeaders(headers: MutableMap<String, Any>) {
        this.headers = headers
    }

    fun addHeader(key: String, value: Any) {
        headers[key] = value
    }
}