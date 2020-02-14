package com.safframework.statemachine

/**
 * 构成状态机的基本单位，状态机在任何特定时间都可处于某一状态。
 * @FileName:
 *          com.safframework.statemachine.State
 * @author: Tony Shen
 * @date: 2020-02-14 21:42
 * @version: V1.0 <描述当前版本功能>
 */
class State(val name: BaseState) {

    private val transitions = hashMapOf<BaseEvent, Transition>()   // Convert to HashMap with event as key
    private val stateActions = mutableListOf<(State) -> Unit>()

    /**
     * Creates a transition from a [State] to another when a [BaseEvent] occurs
     * @param event: Transition event
     * @param targetState: Next state
     * @param init
     */
    fun transition(event: BaseEvent, targetState: BaseState, init: Transition.() -> Unit) {
        val transition = Transition(event, targetState)
        transition.init()

        if (transitions.containsKey(event)) {
            throw StateMachineException("Adding multiple transitions for the same event is invalid")
        }

        transitions.put(event, transition)
    }

    /**
     * Action performed by state
     */
    fun action(action: (State) -> Unit) {
        stateActions.add(action)
    }

    /**
     * Enter the state and run all actions
     */
    fun enter() {
        // Every action takes the current state
        stateActions.forEach { it(this) }
    }

    /**
     * Get the appropriate [Transition] for the [BaseEvent]
     */
    fun getTransitionForEvent(event: BaseEvent): Transition {

        return transitions[event]?:throw IllegalStateException("Event $event isn't registered with state ${this.name}")
    }

    override fun toString(): String {
        return name.javaClass.simpleName
    }

}
