package com.safframework.statemachine

import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.state.SubState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.TestSubState
 * @author: Tony Shen
 * @date: 2020-03-06 16:52
 * @version: V1.0 <描述当前版本功能>
 */
class IDE  : BaseEvent()
class Open_Document  : BaseEvent()
class XXX:BaseEvent()
class YYY:BaseEvent()

class Work : BaseState()
class Computer : BaseState()
class Code : BaseState()
class Write : BaseState()

fun main() {

    val initial =  State(Initial()).entry {
        action {
            println("Entered [${it.name}] State")
        }
    }.exit {
        action {
            println("Exited [${it.name}] State")
        }
    }.transition(Cook(), Eat()) {
        action {
            println("Action: Wash Vegetables")
        }
        action {
            println("Action: Cook")
        }
    }

    val eat = State(Eat()).entry{
        action {
            println("Entered [${it.name}] State")
        }
    }.transition(WashDishes(), Computer()) {
        action {
            println("Action: Wash Dishes")
        }
    }

    val work_initial = State(Work())

    val computer = State(Computer())
        .entry {
            action {
                println("Action: Open Computer")
            }
        }.transition(IDE(), Code())

    val code = State(Code()).entry{
        action {
            println("Entered [${it.name}] State")
        }
        action {
            println("Open IntelliJ IDEA")
        }
    }.transition(Open_Document(), Write())

    val write = State(Write()).entry{
        action {
            println("Entered [${it.name}] State")
        }
    }.transition(XXX(), work_initial.name)

    val work = SubState(work_initial.name, work_initial, computer, code, write).transition(YYY(), Eat())

    val sm = StateMachine.buildStateMachine("tony",initial.name) {
        this.state(initial)
        this.state(eat)
        this.state(work)
    }

    sm.initialize()
    sm.sendEvent(Cook())
    sm.sendEvent(WashDishes())
    sm.sendEvent(IDE())
    sm.sendEvent(Open_Document())
    sm.sendEvent(XXX())
    sm.sendEvent(YYY())
}