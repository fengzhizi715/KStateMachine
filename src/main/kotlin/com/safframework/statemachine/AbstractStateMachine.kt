package com.safframework.statemachine

import com.safframework.statemachine.action.Action
import com.safframework.statemachine.context.DefaultStateContext
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.message.DefaultMessage
import com.safframework.statemachine.message.Message
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition


/**
 * Created by tony on 2020/1/5.
 */
abstract class AbstractStateMachine<S,E>(
    private val states: Map<S, State<S, E>>,
    private val transitions: Map<S, Collection<Transition<S, E>>>,
    private val initialState: State<S, E>,
    private var currentState: State<S, E>,
    private var currentError: Exception
):StateMachine<S,E> {

    @Volatile
    private lateinit var currentEvent: Message<E>               //当前事件

    @Volatile
    private lateinit var currentTransition: Transition<S, E>     //当前转换器

    override fun getInitialState(): State<S, E> = initialState

    override fun getStateMachineError(): Exception = currentError

    override fun getState(): State<S, E> = currentState

    override fun getEvent(): Message<E> = currentEvent

    override fun getStates(): Collection<State<S, E>> = states?.values

    override fun getTransitions(): Map<S, Collection<Transition<S, E>>> = transitions

    override fun transition(): Transition<S, E> = currentTransition

    override fun isComplete(): Boolean = currentState.isEnd() || currentState != null

    override fun sendEvent(event: Message<E>): Boolean {
        return sendEvent(event, false)
    }

    /**
     * 发送事件
     * @param event     事件
     * @param autoDrive 是否自动触发事件扭转状态
     * @return 是否接受事件
     */
    private fun sendEvent(event: Message<E>, autoDrive: Boolean): Boolean {
        var accepted: Boolean = sendEventInternal(event)
        if (accepted && !currentState.isEnd()) { //自动驱动流程
            if (autoDrive) { //如果不是最终状态、不是挂起状态、并且状态机接受了上一事件
                while (!currentState.isEnd() && accepted && !currentState.isSuspend()) {
                    var nextEvent: E? = null
                    for (transition in transitions[currentState.getId()]!!) {
                        if (transition.getSource().getId()!!.equals(currentState.getId())) {
                            nextEvent = transition.getEvent()
                            break
                        }
                    }

                    nextEvent?.let {
                        val newMessage:Message<E> = DefaultMessage<E>(it, event.getHeaders())
                        accepted = sendEventInternal(newMessage)
                    }
                }
            }
        }
        return accepted
    }

    /**
     * 发送事件
     * @param event
     * @return 是否接受事件
     */
    private fun sendEventInternal(event: Message<E>): Boolean {

        var event = event
        return try {
            currentEvent = event

            //找出当前状态所有的转换器
            val trans: Collection<Transition<S, E>>? = transitions[currentState.getId()]

            trans?.takeIf { it.isNotEmpty() }?.let {
                var transitExecuted = false
                for (transition in trans) { //如果事件不是状态关心的
                    if (transition.getEvent() !=  event.getPayload()) {
                        continue
                    }
                    //设置当前转换器
                    currentTransition = transition
                    /*  进入以下流程表示已经找到Transition */
                    val stateContext: StateContext<S, E> = DefaultStateContext(event, transition, transition.getSource(), transition.getTarget())
                    //如果transition.transit()没有执行过
                    if (!transitExecuted) { //转换改变前拦截器
                        //状态扭转前执行action, action执行失败表示不接受事件，返回false
                        val accept = transition.transit(stateContext)
                        //标记已执行
                        transitExecuted = true
                        if (!accept) {

                            stateContext.getException()?.let {
                                setStateMachineError(it)
                            }

                            println("状态扭转失败,source ${currentState.getId()} -> target ${transition.getTarget().getId()} Event ${event.getPayload()}")
                            return false
                        }
                    }
                    //已经执行过transition.transit(),不用再执行，只需要判断guard()
                    //StandardTransition只有一个默认的guard，返回true
                    //ChoiceTransition 每个if/elseif分支有一个guard，但configurer已经确保必须有一个else分支使用guard返回true
                    if (transition.guard().evaluate(stateContext)) { //转换成功
                        return transitionSuccess(transition, stateContext, event)
                    }
                }
            }

            val msg = "没有找到Transition, 状态${currentState.getId()}，事件${event.getPayload()}"
            println(msg)
            setStateMachineError(StateMachineException(msg))
            return false
        } catch (e: java.lang.Exception) {
            setStateMachineError(e)
            currentError = e
            return false
        }
    }

    /**
     * 转换成功
     */
    private fun transitionSuccess(
        transition: Transition<S, E>,
        stateContext: StateContext<S, E>,
        event: Message<E>
    ): Boolean {
        println("状态扭转成功,source ${currentState.getId()} -> target ${transition.getTarget().getId()}")
        //触发state退出绑定的action
        triggerStateAction(currentState.getExitActions(), stateContext)
        //扭转状态
        currentState = transition.getTarget()
        //触发state进入绑定的action
        triggerStateAction(currentState.getEntryActions(), stateContext)

        return true
    }

    /**
     * 触发状态绑定的action
     */
    private fun triggerStateAction(
        actions: Collection<Action<S, E>?>,
        context: StateContext<S, E>
    ) {
        if (actions.isNotEmpty()) {
            for (action in actions) {
                action?.execute(context)
            }
        }
    }
}