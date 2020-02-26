# KStateMachine

使用 Kotlin 特性实现的有限状态机 (FSM) 框架，基于事件驱动。

## 有限状态机定义

有限状态机，（英语：Finite-state machine, FSM），又称有限状态自动机，简称状态机，是表示有限个状态以及在这些状态之间的转移和动作等行为的数学模型。有限状态机体现了两点：首先是离散的，然后是有限的。以下是对状态机抽象定义

* State（状态）：构成状态机的基本单位。 状态机在任何特定时间都可处于某一状态。从生命周期来看有Initial State、End State、Suspend State(挂起状态)

* Event（事件）：导致转换发生的事件活动

* Transitions（转换器）：两个状态之间的定向转换关系，状态机对发生的特定类型事件响应后当前状态由A转换到B。标准转换、选择转、子流程转换多种抽象实现

* Actions（转换操作）：在执行某个转换时执行的具体操作。

* Guards（检测器）：检测器出现的原因是为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态

* Interceptor（拦截器）：对当前状态改变前、后进行监听拦截。

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

![](images/fsm.png)

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

## Feature

* 支持 DSL 构建的 FSM
* 支持全局的拦截器
* 支持进入状态、离开状态的Actions
* 支持 RxJava 2


## TODO：

* 通过配置构建状态机，支持 yml、properties
* 支持 Kotlin Coroutines
* 支持 HSM

