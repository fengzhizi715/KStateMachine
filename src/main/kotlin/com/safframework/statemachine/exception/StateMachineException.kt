package com.safframework.statemachine.exception

import java.text.MessageFormat

/**
 * Created by tony on 2020/1/2.
 */
class StateMachineException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, vararg params: Any?) : super(
        MessageFormat.format(message, *params)
    )

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(cause: Throwable?, message: String?, vararg params: Any?) : super(
        MessageFormat.format(message, *params), cause
    )
}