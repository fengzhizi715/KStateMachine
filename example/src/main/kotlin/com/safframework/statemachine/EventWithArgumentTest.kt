package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.utils.extension.initialState
import com.safframework.statemachine.utils.extension.invoke
import com.safframework.statemachine.utils.extension.state
import com.safframework.statemachine.utils.extension.transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.EventWithArgumentTest
 * @author: Tony Shen
 * @date: 2023/7/13 14:59
 * @version: V1.0 <描述当前版本功能>
 */
class TransitionWithArgumentEvent: Event

fun main() {

    val sm = createStateMachine {
        logger = StateMachine.Logger { println(it) }

        val init = initialState {}

        val state1 = state("state1")
        val state2 = state("state2")

        init {
            transition<TransitionWithArgumentEvent>("TransitionWithArgument1") {
                targetState = state1
            }
        }

        state1 {
            transition<TransitionWithArgumentEvent>("TransitionWithArgument2") {
                targetState = state2
            }
        }
    }

    sm.sendEvent(TransitionWithArgumentEvent(), "test1")
    sm.sendEvent(TransitionWithArgumentEvent(), 2023)
}