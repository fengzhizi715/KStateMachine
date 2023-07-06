package com.safframework.statemachine

import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.statemachine.StateMachine
import com.safframework.statemachine.v2.statemachine.createStateMachine
import com.safframework.statemachine.v2.transition.action
import com.safframework.statemachine.v2.utils.extension.*

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
            entry {
                println("Entered [${name}] State")
            }

            exit {
                println("Exited [${name}] State")
            }
        }

        val eat = state("eat") {
            entry {
                println("Entered [${name}] State")
            }
        }

        val watchTV = state("watchTV") {
            entry {
                println("Entered [${name}] State")
            }
        }

        init {
            transition<CookEvent>("cook") {
                targetState = eat
                action {
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

    machine.sendEvent(CookEvent())
    machine.sendEvent(WashDishesEvent())
}