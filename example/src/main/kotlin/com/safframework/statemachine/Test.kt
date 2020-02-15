package com.safframework.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.Test
 * @author: Tony Shen
 * @date: 2020-02-15 17:54
 * @version: V1.0 <描述当前版本功能>
 */
class Cook : BaseEvent()
class WashDishes: BaseEvent()

class Initial : BaseState()
class Eat : BaseState()
class WatchTV : BaseState()

fun main() {

    val sm = StateMachine.buildStateMachine(Initial()) {

        state(Initial()) {
            action {
                println("Entered [$it]")
            }

            transition(Cook(), Eat()) {
                action {
                    println("Wash Vegetables")
                }

                action {
                    println("Cook")
                }
            }
        }

        state(Eat()) {

            action {
                println("Entered [$it]")
            }

            transition(WashDishes(), WatchTV()) {

                action {
                    println("Wash Dishes")
                }

                action {
                    println("Turn on the TV")
                }
            }
        }

        state(WatchTV()) {

            action {
                println("Entered [$it]")
            }
        }
    }

    sm.initialize()
    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
}