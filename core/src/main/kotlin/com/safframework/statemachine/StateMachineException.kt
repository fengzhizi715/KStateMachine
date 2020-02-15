package com.safframework.statemachine

import java.lang.RuntimeException

/**
 *
 * @FileName:
 *          com.safframework.statemachine.StateMachineException
 * @author: Tony Shen
 * @date: 2020-02-14 21:38
 * @version: V1.0 <描述当前版本功能>
 */
class StateMachineException(message: String) : RuntimeException(message)