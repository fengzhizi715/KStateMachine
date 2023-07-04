package com.safframework.statemachine.v2.transition

import com.safframework.statemachine.v2.TransitionDirectionProducer
import com.safframework.statemachine.v2.domain.Event
import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.state.InternalState
import java.util.concurrent.CopyOnWriteArraySet

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.transition.DefaultTransition
 * @author: Tony Shen
 * @date: 2023/7/4 12:05
 * @version: V1.0 <描述当前版本功能>
 */
open class DefaultTransition<E : Event>(
    override val name: String?,
    override val eventMatcher: EventMatcher<E>,
    sourceState: IState
) : InternalTransition<E> {
    private val _listeners = CopyOnWriteArraySet<Transition.Listener>()
    override val listeners: Collection<Transition.Listener> get() = _listeners
    override val sourceState = sourceState as InternalState

    /**
     * Function that is called during event processing,
     * not during state machine configuration. So it is possible to check some outer (business logic) values in it.
     * If [Transition] does not have target state then [StateMachine] keeps current state
     * when such [Transition] is triggered.
     * This function should not have side effects.
     */
    private var targetStateDirectionProducer: TransitionDirectionProducer<E> = { stay() }

    override var argument: Any? = null

    constructor(
        name: String?,
        eventMatcher: EventMatcher<E>,
        sourceState: IState,
        targetState: IState?
    ) : this(name, eventMatcher, sourceState) {
        targetStateDirectionProducer = if (targetState == null) {
            { stay() }
        } else {
            { targetState(targetState) }
        }
    }

    constructor(
        name: String?,
        eventMatcher: EventMatcher<E>,
        sourceState: IState,
        targetStateDirectionProducer: TransitionDirectionProducer<E>
    ) : this(name, eventMatcher, sourceState) {
        this.targetStateDirectionProducer = targetStateDirectionProducer
    }

    override fun <L : Transition.Listener> addListener(listener: L): L {
        require(_listeners.add(listener)) { "$listener is already added" }
        return listener
    }

    override fun removeListener(listener: Transition.Listener) {
        _listeners.remove(listener)
    }

    override fun isMatchingEvent(event: Event) = eventMatcher.match(event)

    override fun produceTargetStateDirection(policy: TransitionDirectionProducerPolicy<E>) =
        targetStateDirectionProducer(policy)

    override fun toString() = "${this::class.simpleName}${if (name != null) "($name)" else ""}"
}