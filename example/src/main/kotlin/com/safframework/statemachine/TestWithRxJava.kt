package com.safframework.statemachine

import com.safframework.statemachine.rxjava3.enterTransitionObservable
import com.safframework.statemachine.rxjava3.exitTransitionObservable
import com.safframework.statemachine.rxjava3.stateObservable
import com.safframework.statemachine.state.State

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TestWithRxJava
 * @author: Tony Shen
 * @date: 2021-01-04 18:52
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

        this.state(initial)
        this.state(eat)
        this.state(watchTV)
    }

    sm.initialize()
    sm.enterTransitionObservable.subscribe {
        println("enterTransition:"+it.stateContext.getTarget())
    }
    sm.exitTransitionObservable.subscribe {
        println("exitTransition:"+it.stateContext.getTarget())
    }

    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
}