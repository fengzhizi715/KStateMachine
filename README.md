[![@Tony沈哲 on weibo](https://img.shields.io/badge/weibo-%40Tony%E6%B2%88%E5%93%B2-blue.svg)](http://www.weibo.com/fengzhizi715)
[![License](https://img.shields.io/badge/license-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

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
    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
}
```

![](images/fsm.png)

执行结果：

```
Entered [Initial] State
Exited [Initial] State
Action: Wash Vegetables
Action: Cook
Entered [Eat] State
Action: Wash Dishes
Action: Turn on the TV
Entered [WatchTV] State
```

## Feature

* 支持 DSL 的方式构建状态机
* 支持 FSM、HSM
* 支持拦截器、以及全局的拦截器
* 支持进入状态、离开状态的 Actions
* 支持 RxJava 2、RxJava 3

## 最新版本

模块|最新版本
---|:-------------:
statemachine-core|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/statemachine-core/images/download.svg) ](https://bintray.com/fengzhizi715/maven/statemachine-core/_latestVersion)
statemachine-rxjava2|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/statemachine-rxjava2/images/download.svg) ](https://bintray.com/fengzhizi715/maven/statemachine-rxjava2/_latestVersion)
statemachine-rxjava3|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/statemachine-rxjava3/images/download.svg) ](https://bintray.com/fengzhizi715/maven/statemachine-rxjava3/_latestVersion)

## 下载：

```groovy
implementation 'com.safframework.statemachine:statemachine-core:<latest-version>'
```

```groovy
implementation 'com.safframework.statemachine:statemachine-rxjava2:<latest-version>'
```

```groovy
implementation 'com.safframework.statemachine:statemachine-rxjava3:<latest-version>'
```

## TODO：

* 支持 Kotlin Coroutines
* 通过配置的方式构建状态机，支持 yml、properties




