package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.StateTransitionsHelper
 * @author: Tony Shen
 * @date: 2023/7/3 20:42
 * @version: V1.0 <描述当前版本功能>
 */
interface StateTransitionsHelper {
    val transitions: Set<Transition<*>>

    fun <E : Event> addTransition(transition: Transition<E>): Transition<E>

    /**
     * Get transition by name. This might be used to start listening to transition after state machine setup.
     */
    fun findTransition(name: String) = transitions.find { it.name == name }

    /**
     * For internal use only
     */
    fun toState(): IState
}