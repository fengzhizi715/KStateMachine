package com.safframework.statemachine

import com.safframework.statemachine.message.DefaultMessage
import com.safframework.statemachine.message.Message
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition
import java.util.Collection
import java.util.Map


/**
 * Created by tony on 2020/1/5.
 */
abstract class AbstractStateMachine<S,E>(
    private val states: Map<S, State<S, E>>,
    private val transitions: Map<S, Collection<Transition<S, E>>>,
    private val initialState: State<S, E>,
    private val currentState: State<S, E>,
    private val currentError: Exception
):StateMachine<S,E> {

    //当前事件
    @Volatile
    private lateinit var currentEvent: Message<E>

    //当前转换器
    @Volatile
    private lateinit var currentTransition: Transition<S, E>

    override fun getInitialState(): State<S, E> = initialState

    override fun getStateMachineError(): Exception = currentError

    override fun getState(): State<S, E> = currentState

    override fun getEvent(): Message<E> = currentEvent

    override fun getStates(): MutableCollection<State<S, E>> = states?.values()

    override fun getTransitions(): Map<S, Collection<Transition<S, E>>> = transitions

    override fun transition(): Transition<S, E> = currentTransition

    override fun isComplete(): Boolean = currentState.isEnd() || currentState != null
}