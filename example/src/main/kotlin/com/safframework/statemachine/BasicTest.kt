package com.safframework.statemachine

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.transition.action
import com.safframework.statemachine.utils.extension.*

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
    val sm = createStateMachine("test") {
//        logger = StateMachine.Logger { println(it) }

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

    sm.sendEvent(CookEvent())
    sm.sendEvent(WashDishesEvent())
}