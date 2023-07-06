package com.safframework.statemachine.exception

import java.lang.RuntimeException

/**
 *
 * @FileName:
 *          com.safframework.statemachine.exception.StateMachineException
 * @author: Tony Shen
 * @date: 2020-02-14 21:38
 * @version: V1.0 <描述当前版本功能>
 */
class StateMachineException(message: String) : RuntimeException(message)