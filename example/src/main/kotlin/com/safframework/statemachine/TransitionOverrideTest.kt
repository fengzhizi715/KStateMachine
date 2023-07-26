package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.utils.extension.*

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TransitionOverrideTest
 * @author: Tony Shen
 * @date: 2023/7/26 10:36
 * @version: V1.0 <描述当前版本功能>
 */
class OverrideEvent: Event

fun main() {
    val sm = createStateMachine {
        logger = StateMachine.Logger { println(it) }

        lateinit var nested1: State

        val finalState = finalState("final")

        val init = initialState("init") {
            nested1 = initialState("nested1")
        }

        val state1 = state("state1")

        init {
            transition<OverrideEvent> {
                targetState = finalState
            }
        }

        nested1 {
            transitionOn<OverrideEvent> {
                targetState = { state1 }
            }
        }
    }

    sm.sendEvent(OverrideEvent())
}