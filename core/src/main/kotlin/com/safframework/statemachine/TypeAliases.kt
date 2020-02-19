package com.safframework.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TypeAliases
 * @author: Tony Shen
 * @date: 2020-02-19 23:54
 * @version: V1.0 <描述当前版本功能>
 */
typealias StateAction = (State) -> Unit

typealias TransitionAction = (Transition) -> Unit

typealias Guard = ()->Boolean