# KStateMachine

基于 Kotlin 特性实现的有限状态机 (FSM) 框，基于事件驱动。

```kotlin
fun main() {

    val sm = StateMachine.buildStateMachine(Initial()) {

        state(Initial()) {
            action {
                println("Entered [$it] State")
            }

            transition(Cook(), Eat()) {
                action {
                    println("Action: Wash Vegetables")
                }

                action {
                    println("Action: Cook")
                }
            }
        }

        state(Eat()) {

            action {
                println("Entered [$it] State")
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

            action {
                println("Entered [$it] State")
            }
        }
    }

    sm.initialize()
    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
}
```

执行结果：

```
Entered [Initial] State
Action: Wash Vegetables
Action: Cook
Entered [Eat] State
Action: Wash Dishes
Action: Turn on the TV
Entered [WatchTV] State
```