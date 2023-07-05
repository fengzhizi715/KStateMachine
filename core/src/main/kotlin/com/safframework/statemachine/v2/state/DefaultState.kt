package com.safframework.statemachine.v2.state

import com.safframework.statemachine.v2.domain.ChildMode
import com.safframework.statemachine.v2.domain.DataEvent
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.transition.TransitionParams
import com.safframework.statemachine.v2.transition.Transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.state.DefaultState
 * @author: Tony Shen
 * @date: 2023/7/4 10:26
 * @version: V1.0 <描述当前版本功能>
 */
open class DefaultState(name: String? = null, childMode: ChildMode = ChildMode.EXCLUSIVE) : BaseStateImpl(name, childMode), State

open class DefaultDataState<out D>(name: String? = null, childMode: ChildMode = ChildMode.EXCLUSIVE) :
    BaseStateImpl(name, childMode), DataState<D> {
    private var _data: D? = null
    override val data: D get() = checkNotNull(_data) { "Data is not set. Is the state active?" }

    override fun onDoEnter(transitionParams: TransitionParams<*>) {
        if (this == transitionParams.direction.targetState) {
            @Suppress("UNCHECKED_CAST")
            val event = transitionParams.event as? DataEvent<D>
            checkNotNull(event) { "${transitionParams.event} does not contain data required by $this" }
            _data = event.data
        } else {
            error(
                "$this is implicitly activated, this might be a result of a cross-level transition. " +
                        "Currently there is no way to get data for this state."
            )
        }
    }

    override fun onDoExit(transitionParams: TransitionParams<*>) {
        _data = null
    }
}

open class DefaultFinalState(name: String? = null) : DefaultState(name), FinalState {
    override fun <E : Event> addTransition(transition: Transition<E>) = super<FinalState>.addTransition(transition)
}

open class DefaultFinalDataState<out D>(name: String? = null) : DefaultDataState<D>(name), FinalDataState<D> {
    override fun <E : Event> addTransition(transition: Transition<E>) = super<FinalDataState>.addTransition(transition)
}
