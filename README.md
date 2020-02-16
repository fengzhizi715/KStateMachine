# KStateMachine

基于 Kotlin 特性实现的有限状态机 (FSM) 框，基于事件驱动。

```kotlin
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
```