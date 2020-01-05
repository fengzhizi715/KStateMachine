package com.safframework.statemachine

import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition


/**
 * Created by tony on 2020/1/5.
 */
abstract class AbstractStateMachine<S,E>(
    states: Map<S, State<S, E>?>,
    transitions: Map<S, kotlin.collections.Collection<Transition<S, E>?>?>,
    initialState: State<S, E>,
    currentState: State<S, E>,
    currentError: Exception
):StateMachine<S,E> {


}