[![@Tony沈哲 on weibo](https://img.shields.io/badge/weibo-%40Tony%E6%B2%88%E5%93%B2-blue.svg)](http://www.weibo.com/fengzhizi715)
[![License](https://img.shields.io/badge/license-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![](https://jitpack.io/v/fengzhizi715/KStateMachine.svg)](https://jitpack.io/#fengzhizi715/KStateMachine)

# KStateMachine

使用 Kotlin 特性实现的 FSM、HSM 框架，基于事件驱动。

## 有限状态机定义

有限状态机，（英语：Finite-state machine, FSM），又称有限状态自动机，简称状态机，是表示有限个状态以及在这些状态之间的转移和动作等行为的数学模型。有限状态机体现了两点：首先是离散的，然后是有限的。以下是对状态机抽象定义

* State（状态）：构成状态机的基本单位。 状态机在任何特定时间都可处于某一状态。从生命周期来看有Initial State、End State、Suspend State(挂起状态)

* Event（事件）：导致转换发生的事件活动

* Transitions（转换器）：两个状态之间的定向转换关系，状态机对发生的特定类型事件响应后当前状态由A转换到B。标准转换、选择转、子流程转换多种抽象实现

* Actions（转换操作）：在执行某个转换时执行的具体操作。

* Guards（检测器）：检测器出现的原因是为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态

* Interceptor（拦截器）：对当前状态改变前、后进行监听拦截。

## 分层状态机定义

分层状态机，（英语：Hierarchical State Machine, HSM）

分层状态机是一种用于描述系统行为和控制流的模型，它将系统划分为多个层级，并将每个层级表示为一个状态机。每个状态机描述了一个特定的状态集合以及状态之间的转移规则。每个状态机都有自己的输入和输出，可以与其他状态机进行交互，从而形成整个系统的行为。

## 状态机的使用

### FSM 的使用

```kotlin
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
```

![](images/fsm.png)

执行结果：

```
Entered [init] State
Action: Wash Vegetables
Action: Cook
Exited [init] State
Entered [eat] State
Entered [watchTV] State
```

### HSM 的使用

## Feature

* 支持 DSL 的方式构建状态机
* 支持 FSM、HSM(Nested State)
* 支持 Event 传递参数
* 支持拦截器以及全局的拦截器
* 支持 State 的 entry 和 exit 的 Action
* 支持 LOCAL、EXTERNAL 类型的 Transition
* 支持 Transition 时执行的 Action
* 支持 Transition 的 extend 和 override
* 支持将状态机导出成 PlantUML、JSON
* 支持异常处理

## 最新版本

模块|最新版本
---|:-------------:
statemachine-core|[![](https://jitpack.io/v/fengzhizi715/KStateMachine.svg)](https://jitpack.io/#fengzhizi715/KStateMachine)

将它添加到项目的 root build.gradle 中：

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

## 下载：

```groovy
implementation 'com.github.fengzhizi715.KStateMachine:core:<latest-version>'
```

## TODO：

* 支持 Kotlin Coroutines
* 支持导出状态图
