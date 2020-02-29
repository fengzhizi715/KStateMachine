package com.safframework.statemachine

import com.safframework.statemachine.context.DefaultStateContext
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.interceptor.GlobalInterceptor
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.state.State
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @FileName:
 *          com.safframework.statemachine.StateMachine
 * @author: Tony Shen
 * @date: 2020-02-14 21:50
 * @version: V1.0 <描述当前版本功能>
 */
class StateMachine private constructor(var name: String?=null,private val initialState: BaseState) {

    private lateinit var currentState: State    // 当前状态
    private val states = mutableListOf<State>() // 状态列表
    private val initialized = AtomicBoolean(false) // 是否初始化
    private var globalInterceptor: GlobalInterceptor?=null
    private val transitionCallbacks: MutableList<TransitionCallback> = mutableListOf()
    private val path = mutableListOf<StateMachine>()
    internal val descendantStates: Set<State> = mutableSetOf()

    /**
     * 设置状态机全局的拦截器，使用时必须要在 initialize() 之前
     * @param event: 状态机全局的拦截器
     */
    fun interceptor(globalInterceptor: GlobalInterceptor):StateMachine {
        this.globalInterceptor = globalInterceptor
        return this
    }

    /**
     * 初始化状态机，并进入初始化状态
     */
    fun initialize() {
        if(initialized.compareAndSet(false, true)){
            currentState = getState(initialState)
            currentState.owner = this@StateMachine
            path.add(0, this)
            currentState.addParent(this)
            descendantStates.plus(currentState)
            globalInterceptor?.stateEntered(currentState)
            currentState.enter()
        }
    }

    /**
     * 向状态机添加 State
     */
    fun state(stateName: BaseState, init: State.() -> Unit):StateMachine {
        val state = State(stateName).apply{
            init()
            owner = this@StateMachine
            addParent(this@StateMachine)
            descendantStates.plus(this.getDescendantStates())
        }

        states.add(state)
        return this
    }

    /**
     * 通过状态名称获取状态
     */
    private fun getState(stateType: BaseState): State = states.firstOrNull { stateType.javaClass == it.name.javaClass } ?: throw NoSuchElementException(stateType.javaClass.canonicalName)

    /**
     * 向状态机发送 Event，执行状态转换
     */
    @Synchronized
    fun sendEvent(e: BaseEvent) {
        try {
            val transition = currentState.getTransitionForEvent(e)

            globalInterceptor?.transitionStarted(transition)

            val stateContext: StateContext = DefaultStateContext(e, transition, transition.getSourceState(), transition.getTargetState())

            //状态转换之前执行的 action(Transition 内部的 action), action执行失败表示不接受事件，返回false
            val accept = transition.transit(stateContext)

            if (!accept) {
                //状态机发生异常
                globalInterceptor?.stateMachineError(this, StateMachineException("状态转换失败,source ${currentState.name} -> target ${transition.getTargetState()} Event ${e}"))
                return
            }

            val guard = transition.getGuard()?.invoke()?:true

            if (guard) {
                transitionSuccess(transition,stateContext)
            } else {
                println("$transition 失败")

                globalInterceptor?.stateMachineError(this, StateMachineException("状态转换失败: guard [${guard}], 状态 [${currentState.name}]，事件 [${e.javaClass.simpleName}]"))
            }
        } catch (exception:Exception) {

            globalInterceptor?.stateMachineError(this, StateMachineException("This state [${this.currentState.name}] doesn't support transition on ${e.javaClass.simpleName}"))
        }
    }

    /**
     * 状态转换成功
     * @param transition
     * @param stateContext
     */
    private fun transitionSuccess(transition:Transition, stateContext: StateContext) {
        getState(transition.getSourceState()).exit()

        val state = transition.applyTransition { getState(stateContext.getTarget()) }

        val callbacks = transitionCallbacks.toList()

        globalInterceptor?.apply {
            stateContext(stateContext)
            transition(transition)
            stateExited(currentState)
        }

        callbacks.forEach { callback ->
            callback.enteringState(this, stateContext.getSource(), transition, stateContext.getTarget())
        }

        state.enter()

        callbacks.forEach { callback ->
            callback.enteredState(this, stateContext.getSource(), transition, stateContext.getTarget())
        }

        globalInterceptor?.apply {
            stateEntered(state)
            stateChanged(currentState,state)
            transitionEnded(transition)
        }

        currentState = state
    }

    @Synchronized
    fun getCurrentState(): State? = if (isCurrentStateInitialized()) this.currentState else null

    private fun isCurrentStateInitialized() = ::currentState.isInitialized

    fun processEvent(event: BaseEvent): Boolean {
        return currentState?.processEvent(event) ?: false
    }

    internal fun executeTransition(transition: Transition, event: BaseEvent) {
    }

    internal fun addParent(parent: StateMachine) {
        path.add(0, parent)
        states.forEach {
            it.addParent(parent)
        }
    }

    fun getAllActiveStates(): Set<State> {

        if (!isCurrentStateInitialized()) {
            return emptySet()
        }

        val activeStates: MutableSet<State> = mutableSetOf(currentState)
        activeStates.addAll(currentState.getAllActiveStates())
        return activeStates.toSet()
    }

    /**
     * 注册 TransitionCallback
     */
    fun registerCallback(transitionCallback: TransitionCallback) = transitionCallbacks.add(transitionCallback)

    /**
     * 取消 TransitionCallback
     */
    fun unregisterCallback(transitionCallback: TransitionCallback) = transitionCallbacks.remove(transitionCallback)

    companion object {

        /**
         * @param name 状态机的名称
         * @param initialStateName 初始化状态机的 block
         */
        fun buildStateMachine(name:String = "StateMachine", initialStateName: BaseState, init: StateMachine.() -> Unit): StateMachine  = StateMachine(name,initialStateName).apply{
            init()
        }
    }
}