package com.safframework.statemachine

import com.safframework.statemachine.domain.DataEvent
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.DataState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.utils.extension.*

/**
 *
 * @FileName:
 *          com.safframework.statemachine.GuardTransitionTest
 * @author: Tony Shen
 * @date: 2023/7/11 15:57
 * @version: V1.0 <描述当前版本功能>
 */
class ChoiceEvent(override val data: Int) : DataEvent<Int>

fun main() {
    val sm = createStateMachine {
        logger = StateMachine.Logger { println(it) }

        lateinit var choice: DataState<Int>

        initialState("init") {
            dataTransitionOn<ChoiceEvent,Int>("choice") {
                guard = {
                    it.data > 10
                }
                targetState = { choice }
            }
        }

        choice = dataState {
            entry {
                println(this.data)
            }
        }
    }

    sm.sendEvent(ChoiceEvent(5))
    sm.sendEvent(ChoiceEvent(20))
}