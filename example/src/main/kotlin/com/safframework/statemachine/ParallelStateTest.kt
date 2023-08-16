package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.utils.extension.*

/**
 *
 * @FileName:
 *          com.safframework.statemachine.ParallelStateTest
 * @author: Tony Shen
 * @date: 2023/8/16 14:26
 * @version: V1.0 <描述当前版本功能>
 */
class ParallelTransitionEvent1: Event
class ParallelTransitionEvent2: Event

fun main() {

    val sm = createStateMachine("parallel state") {
        logger = StateMachine.Logger { println(it) }

        lateinit var state1: State

        val finalState = finalState("final")

        initialState("init") {

            transitionOn<ParallelTransitionEvent1> {
                targetState = { state1 }
            }
        }

        state1 = state("Top level 1",childMode = ChildMode.PARALLEL) {
            state("Nested 11")
            state("Nested 12")

            transition<ParallelTransitionEvent2> {
                targetState = finalState
            }
        }
    }

    sm.sendEvent(ParallelTransitionEvent1())
    sm.sendEvent(ParallelTransitionEvent2())
}