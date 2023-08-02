package com.safframework.statemachine.visitors

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.IFinalState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.InternalTransition
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy
import com.safframework.statemachine.utils.extension.isFinal
import java.io.Serializable
import kotlin.reflect.full.createInstance

/**
 *
 * @FileName:
 *          com.safframework.statemachine.visitors.ExportJSONVisitor
 * @author: Tony Shen
 * @date: 2023/7/31 20:20
 * @version: V1.0 <描述当前版本功能>
 */
data class Quadruple<A,B,C,D>(var first: A, var second: B, var third: C, var fourth: D): Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

class ExportJSONVisitor: Visitor {
    private val builder = StringBuilder()

    private val stateMap = mutableMapOf<String,MutableList<String>?>()
    private val stateInfoMap = mutableMapOf<String,IState>()
    private val transitionMap = mutableMapOf<String,MutableList<Quadruple<String,String,String,String>>>()

    fun export() = builder.toString()

    override fun visit(machine: StateMachine) {
        // 先遍历一遍状态机
        processStateBody(machine)

        // 然后开始进行拼装 json 字符串
        val machineName = machine.name ?: machine.graphName()
        val indent = 1
        line("{")
        line("\"machineName\":\"${machineName}\",",indent)
        val sm = stateMap[machineName]
        line("\"states\":[",indent)
        var count=0
        val initName = machine.initialState?.graphName()?:""
        sm?.forEach {stateName ->
            count++

            processState(stateName,initName,indent)

            if (count != sm.size) {
                line("},",indent)
            } else {
                line("}",indent)
            }
        }

        line("]",indent)
        line("}")
    }

    private fun processState(stateName:String, initName:String, indent:Int, flag:Boolean=false, endWithComma:Boolean=false) {
        var tempIndent = indent
        line("{",tempIndent)
        tempIndent++
        line("\"name\":\"${stateName}\",",tempIndent)
        val state = stateInfoMap[stateName]
        line("\"isInit\":${stateName == initName},",tempIndent)
        line("\"isFinal\":${state?.isFinal()?:false},",tempIndent)
        transitionMap[stateName]?.let { list->
            if(list.size>0) {
                line("\"transitions\":[",tempIndent)
                var size = 0
                list.forEach { transition->
                    size++
                    line("{",tempIndent)
                    tempIndent++
                    line("\"name\":\"${transition.third}\",",tempIndent)
                    line("\"sourceState\":\"${transition.first}\",",tempIndent)
                    line("\"targetState\":\"${transition.second}\",",tempIndent)
                    line("\"event\":\"${transition.fourth}\"",tempIndent)
                    tempIndent--
                    if (size != list.size) {
                        line("},",tempIndent)
                    } else {
                        line("}",tempIndent)
                    }
                }
                line("],",tempIndent)
            } else {
                line("\"transitions\":[],",tempIndent)
            }
        }?: run {
            line("\"transitions\":null,",tempIndent--)
        }

        tempIndent = indent
        val subStates = stateMap[stateName]
        val subInitName = stateInfoMap[stateName]?.initialState?.graphName() ?: ""
        tempIndent++
        if (subStates == null || subStates?.size==0) {
            line("\"subStates\":null",tempIndent)
        } else {
            line("\"subStates\":[",tempIndent)
            var size = 0
            subStates.forEach {
                size++
                if (size!=subStates.size) {
                    processState(it,subInitName,tempIndent,true,true)
                } else {
                    processState(it,subInitName,tempIndent,true,false)
                }
            }
            line("]",tempIndent)
        }

        if (flag) {
            tempIndent--
            if (endWithComma) {
                line("},",tempIndent)
            } else {
                line("}",tempIndent)
            }
        }
    }

    override fun visit(state: IState) {
        if (state.states.isEmpty()) {
            stateMap[state.graphName()] = mutableListOf()
        } else {
            stateMap[state.graphName()] = mutableListOf()
            processStateBody(state)
        }
    }

    override fun <E : Event> visit(transition: Transition<E>) {
        transition as InternalTransition<E>

        val sourceState = transition.sourceState.graphName()
        val eventClass = transition.eventMatcher.eventClass
        val targetState = transition.produceTargetStateDirection(TransitionDirectionProducerPolicy.CollectTargetStatesPolicy()).targetState
            ?: transition.produceTargetStateDirection(TransitionDirectionProducerPolicy.DefaultPolicy(eventClass.createInstance())).targetState
            ?: return

        val list:MutableList<Quadruple<String,String,String,String>> = transitionMap[sourceState] ?: arrayListOf()

        val transitionQuadruple = Quadruple(sourceState, targetState.graphName(), label(transition.name), eventClass.qualifiedName?:"")
        list.add(transitionQuadruple)

        transitionMap[sourceState] = list
    }

    private fun processStateBody(state: IState) {
        val states = state.states.toList()
        val list:MutableList<String> = stateMap[state.graphName()] ?: arrayListOf()

        // visit child states
        for (s in states.indices) {
            stateInfoMap[states[s].graphName()] = states[s]
            list.add(states[s].graphName())
            visit(states[s])
//            if (s != states.lastIndex && state.childMode == ChildMode.PARALLEL)
//                line(PARALLEL)
        }

        stateMap[state.graphName()] = list
        stateInfoMap[state.graphName()] = state

        // visit transitions
        states.flatMap { it.transitions }.forEach { visit(it) }

//        // add finish transitions
//        states.filterIsInstance<IFinalState>()
//            .forEach { line("${it.graphName()} --> ${STAR}") }
    }

    private fun line(text: String,indent:Int = 0) = builder.appendLine(SINGLE_INDENT.repeat(indent) + text)

    private companion object {
        const val SINGLE_INDENT = "    "
        const val PARALLEL = "--"

        fun IState.graphName() = name?.replace(" ", "_") ?: javaClass.simpleName

        fun label(name: String?) = if (name != null) "$name" else ""
    }
}

fun StateMachine.exportToJSON() = with(ExportJSONVisitor()) {
    accept(this)
    export()
}

