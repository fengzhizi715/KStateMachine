package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.utils.extension.*
import com.safframework.statemachine.visitors.exportToPlantUml

/**
 *
 * @FileName:
 *          com.safframework.statemachine.NestedStateTest
 * @author: Tony Shen
 * @date: 2023/7/7 09:58
 * @version: V1.0 <描述当前版本功能>
 */
class CrossLevelTransitionEvent1: Event
class CrossLevelTransitionEvent2: Event
class EndEvent: Event

fun main() {
    val sm = createStateMachine {
        logger = StateMachine.Logger { println(it) }

        lateinit var nested12: State
        lateinit var nested22: State

        val finalState = finalState("final")

        initialState("Top level 1") {
            initialState("Nested 11") {
                transitionOn<CrossLevelTransitionEvent1> {
                    targetState = {
                        nested22
                    }
                }
            }

            nested12 = state("Nested 12") {
                transition<EndEvent> {
                    targetState = finalState
                }
            }
        }

        state("Top level 2") {
            initialState("Nested 21")

            nested22 = state("Nested 22") {
                transition<CrossLevelTransitionEvent2> {
                    targetState = nested12
                }
            }
        }
    }

    sm.sendEvent(CrossLevelTransitionEvent1())
    sm.sendEvent(CrossLevelTransitionEvent2())
    sm.sendEvent(EndEvent())
}