package com.safframework.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.Test
 * @author: Tony Shen
 * @date: 2020-02-15 17:54
 * @version: V1.0 <描述当前版本功能>
 */
class Cook : BaseEvent()      // 烧菜
class WashDishes: BaseEvent() // 洗碗

class Initial : BaseState()   // 初始化状态
class Eat : BaseState()       // 吃饭状态
class WatchTV : BaseState()   // 看电视状态

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