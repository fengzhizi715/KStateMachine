package com.safframework.statemachine.transition

import com.safframework.statemachine.utils.TransitionAction
import com.safframework.statemachine.utils.TransitionDirectionProducer
import com.safframework.statemachine.domain.Event
import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.InternalState
import java.util.concurrent.CopyOnWriteArraySet

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.DefaultTransition
 * @author: Tony Shen
 * @date: 2023/7/4 12:05
 * @version: V1.0 <描述当前版本功能>
 */
open class DefaultTransition<E : Event>(
    override val name: String?,
    override val eventMatcher: EventMatcher<E>,
    override val type: TransitionType,
    sourceState: IState
) : InternalTransition<E> {
    private val _actions = CopyOnWriteArraySet<TransitionAction>()
    override val actions: Collection<TransitionAction> get() = _actions
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
        type: TransitionType,
        sourceState: IState,
        targetState: IState?
    ) : this(name, eventMatcher, type, sourceState) {
        targetStateDirectionProducer = if (targetState == null) {
            { stay() }
        } else {
            { targetState(targetState) }
        }
    }

    constructor(
        name: String?,
        eventMatcher: EventMatcher<E>,
        type: TransitionType,
        sourceState: IState,
        targetStateDirectionProducer: TransitionDirectionProducer<E>
    ) : this(name, eventMatcher, type, sourceState) {
        this.targetStateDirectionProducer = targetStateDirectionProducer
    }

    override fun <A: TransitionAction> addAction(action: A): A {
        require(_actions.add(action)) { "$action is already added" }
        return action
    }

    override fun removeAction(action: TransitionAction) {
        _actions.remove(action)
    }

    override fun isMatchingEvent(event: Event) = eventMatcher.match(event)

    override fun produceTargetStateDirection(policy: TransitionDirectionProducerPolicy<E>) = targetStateDirectionProducer(policy)

    override fun toString() = "${this::class.simpleName}${if (name != null) "[$name]" else ""}"
}