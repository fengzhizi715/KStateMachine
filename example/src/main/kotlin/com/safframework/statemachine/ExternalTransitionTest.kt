package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.transition.TransitionType
import com.safframework.statemachine.utils.extension.*

/**
 *
 * @FileName:
 *          com.safframework.statemachine.ExternalTransitionTest
 * @author: Tony Shen
 * @date: 2023/8/16 16:53
 * @version: V1.0 <描述当前版本功能>
 */
class LocalTransitionEvent: Event
class ExternalTransitionEvent: Event

fun main() {
    val sm = createStateMachine {
        logger = StateMachine.Logger { println(it) }

        lateinit var nested12: State
        lateinit var nested13: State

        val finalState = finalState("final")

        initialState("Top level 1") {
            initialState("Nested 11") {
                transitionOn<LocalTransitionEvent> {
                    targetState = { nested12 }
                }
            }

            nested12 = state("Nested 12") {
                transitionOn<ExternalTransitionEvent> {
                    targetState = { nested13 }
                    type = TransitionType.EXTERNAL
                }
            }

            nested13 = state("Nested 13") {
                transition<EndEvent> {
                    targetState = finalState
                }
            }
        }
    }

    sm.sendEvent(LocalTransitionEvent())
    sm.sendEvent(ExternalTransitionEvent())
    sm.sendEvent(EndEvent())
}