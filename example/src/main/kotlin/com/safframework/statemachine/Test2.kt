package com.safframework.statemachine

import com.safframework.statemachine.state.State

/**
 *
 * @FileName:
 *          com.safframework.statemachine.Test2
 * @author: Tony Shen
 * @date: 2020-03-06 16:42
 * @version: V1.0 <描述当前版本功能>
 */
fun main() {

    val initial =  State(Initial()).entry {
        action {
            println("Entered [${it.name}] State")
        }
    }.exit {
        action {
            println("Exited [${it.name}] State")
        }
    }.transition(Cook(), Eat()) {

        action {
            println("Action: Wash Vegetables")
        }

        action {
            println("Action: Cook")
        }
    }

    val eat = State(Eat()).entry{
        action {
            println("Entered [${it.name}] State")
        }
    }.transition(WashDishes(), WatchTV()) {

        action {
            println("Action: Wash Dishes")
        }

        action {
            println("Action: Turn on the TV")
        }
    }

    val watchTV = State(WatchTV())
        .entry {
            action {
                println("Entered [${it.name}] State")
            }
        }

    val sm = StateMachine.buildStateMachine(initialStateName = initial.name) {

        state(initial)
        state(eat)
        state(watchTV)
    }

    sm.initialize()
    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
}