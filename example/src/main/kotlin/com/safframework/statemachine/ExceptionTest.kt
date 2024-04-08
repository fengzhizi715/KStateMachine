package com.safframework.statemachine

import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine
import com.safframework.statemachine.transition.action
import com.safframework.statemachine.transition.complete
import com.safframework.statemachine.utils.extension.*

/**
 *
 * @FileName:
 *          com.safframework.statemachine.ExceptionTest
 * @author: Tony Shen
 * @date: 2024/4/8 11:08
 * @version: V1.0 <描述当前版本功能>
 */
fun main() {
    val sm = createStateMachine("test") {
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
                complete {
                    val i = 0
                    println( 1/i )
                }
            }
        }

        eat {
            transition<WashDishesEvent>("washDishes")  {
                targetState = watchTV
            }
        }
    }

    try {
        sm.sendEvent(CookEvent())
        sm.sendEvent(WashDishesEvent())
    } catch (e: StateMachineException) {
        e.printStackTrace()

        // You can handle exception yourself
    }
}