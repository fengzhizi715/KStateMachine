package com.safframework.statemachine.visitors

import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.state.IFinalState
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.isNeighbor
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.transition.InternalTransition
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.transition.TransitionDirectionProducerPolicy
import kotlin.reflect.full.createInstance

/**
 *
 * @FileName:
 *          com.safframework.statemachine.visitors.ExportPlantUmlVisitor
 * @author: Tony Shen
 * @date: 2023/7/5 14:58
 * @version: V1.0 <描述当前版本功能>
 */
class ExportPlantUmlVisitor : Visitor {
    private val builder = StringBuilder()
    private var indent = 0
    private val crossLevelTransitions = mutableListOf<String>()

    fun export() = builder.toString()

    override fun visit(machine: StateMachine) {
        line("@startuml")
        line("hide empty description")

        processStateBody(machine)
        crossLevelTransitions.forEach { line(it) }

        line("@enduml")
    }

    override fun visit(state: IState) {
        if (state.states.isEmpty()) {
            line("state ${state.graphName()}")
        } else {
            line("state ${state.graphName()} {")
            ++indent
            processStateBody(state)
            --indent
            line("}")
        }
    }

    /**
     * PlantUML cannot show correctly cross level transitions to nested states.
     * It requires to see all states declarations first to provide correct rendering,
     * so we have to store them to print after state declaration.
     */
    override fun <E : Event> visit(transition: Transition<E>) {
        transition as InternalTransition<E>

        val sourceState = transition.sourceState.graphName()
        val eventClass = transition.eventMatcher.eventClass
        val targetState = transition.produceTargetStateDirection(TransitionDirectionProducerPolicy.CollectTargetStatesPolicy()).targetState
            ?: transition.produceTargetStateDirection(TransitionDirectionProducerPolicy.DefaultPolicy(eventClass.createInstance())).targetState
            ?: return

        val transitionString = "$sourceState --> ${targetState.graphName()}${label(transition.name)}"

        if (transition.sourceState.isNeighbor(targetState))
            line(transitionString)
        else
            crossLevelTransitions.add(transitionString)
    }

    private fun processStateBody(state: IState) {
        val states = state.states.toList()
        // visit child states
        for (s in states.indices) {
            visit(states[s])
            if (s != states.lastIndex && state.childMode == ChildMode.PARALLEL)
                line(PARALLEL)
        }

        // add initial transition
        line("")
        val initialState = state.initialState
        if (initialState != null)
            line("$STAR --> ${initialState.graphName()}")

        // visit transitions
        states.flatMap { it.transitions }.forEach { visit(it) }

        // add finish transitions
        states.filterIsInstance<IFinalState>()
            .forEach { line("${it.graphName()} --> $STAR") }
    }

    private fun line(text: String) = builder.appendLine(SINGLE_INDENT.repeat(indent) + text)

    private companion object {
        const val STAR = "[*]"
        const val SINGLE_INDENT = "    "
        const val PARALLEL = "--"
        fun IState.graphName() = name?.replace(" ", "_") ?: "State[${javaClass.simpleName}]"
        fun label(name: String?) = if (name != null) " : $name" else ""
    }
}

fun StateMachine.exportToPlantUml() = with(ExportPlantUmlVisitor()) {
    accept(this)
    export()
}
