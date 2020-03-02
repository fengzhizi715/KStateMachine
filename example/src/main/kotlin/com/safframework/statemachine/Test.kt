package com.safframework.statemachine

import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState

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

    val sm = StateMachine.buildStateMachine(initialStateName = Initial()) {

        state(Initial()) {

            entry {
                action {
                    println("Entered [${it.name}] State")
                }
            }

            transition(Cook(), Eat()) {

                action {
                    println("Action: Wash Vegetables")
                }

                action {
                    println("Action: Cook")
                }
            }

            exit {
                action {
                    println("Exited [${it.name}] State")
                }
            }
        }

        state(Eat()) {

            entry{
                action {
                    println("Entered [${it.name}] State")
                }
            }

            transition(WashDishes(), WatchTV()) {

                action {
                    println("Action: Wash Dishes")
                }

                action {
                    println("Action: Turn on the TV")
                }
            }
        }

        state(WatchTV()) {

            entry{
                action {
                    println("Entered [${it.name}] State")
                }
            }
        }
    }

    sm.initialize()
    sm.processEvent(Cook())
    sm.processEvent(WashDishes())
}