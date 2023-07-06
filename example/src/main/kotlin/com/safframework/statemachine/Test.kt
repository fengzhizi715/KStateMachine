package com.safframework.statemachine

import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.statemachine.StateMachine
import com.safframework.statemachine.v2.statemachine.createStateMachine
import com.safframework.statemachine.v2.transition.onAction
import com.safframework.statemachine.v2.utils.extension.*
import com.safframework.statemachine.v2.visitors.exportToPlantUml

/**
 *
 * @FileName:
 *          com.safframework.statemachine.Test
 * @author: Tony Shen
 * @date: 2023/7/6 16:46
 * @version: V1.0 <描述当前版本功能>
 */
class CookEvent : Event
class WashDishesEvent : Event

fun main() {
    val machine = createStateMachine("test") {
        logger = StateMachine.Logger { println(it) }

        val init = initialState("init") {
            onEntry {
                println("Entered [${name}] State")
            }

            onExit {
                println("Exited [${name}] State")
            }
        }

        val eat = state("eat") {
            onEntry {
                println("Entered [${name}] State")
            }
        }

        val watchTV = state("watchTV") {
            onEntry {
                println("Entered [${name}] State")
            }
        }

        init {
            transition<CookEvent>("cook") {
                targetState = eat
                onAction {
                    println("Action: Wash Vegetables")
                    println("Action: Cook")
                }
            }
        }

        eat{
            transition<WashDishesEvent>("washDishes")  {
                targetState = watchTV
            }
        }
    }

    machine.processEvent(CookEvent())
    machine.processEvent(WashDishesEvent())
}