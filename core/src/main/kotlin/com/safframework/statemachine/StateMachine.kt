package com.safframework.statemachine

import com.safframework.statemachine.context.DefaultStateContext
import com.safframework.statemachine.context.StateContext
import com.safframework.statemachine.exception.StateMachineException
import com.safframework.statemachine.interceptor.GlobalInterceptor
import com.safframework.statemachine.interceptor.Interceptor
import com.safframework.statemachine.model.BaseEvent
import com.safframework.statemachine.model.BaseState
import com.safframework.statemachine.state.State
import com.safframework.statemachine.transition.Transition
import com.safframework.statemachine.transition.TransitionType
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
    private val states = mutableListOf<State>() // 状态的列表
    private val initialized = AtomicBoolean(false)            // 是否初始化，保证状态机只初始化一次
    private var globalInterceptor: GlobalInterceptor?=null               // 全局的拦截器
    private val interceptors: MutableList<Interceptor> = mutableListOf() // 拦截器的列表
    private val path = mutableListOf<StateMachine>()
    internal val descendantStates: MutableSet<State> = mutableSetOf()
    lateinit var container:State

    /**
     * 设置状态机全局的拦截器，使用时必须要在 initialize() 之前
     * @param event: 状态机全局的拦截器
     */
    fun globalInterceptor(globalInterceptor: GlobalInterceptor):StateMachine {
        this.globalInterceptor = globalInterceptor
        return this
    }

    /**
     * 初始化状态机，并进入初始化状态，保证只初始化一次防止多次初始化
     */
    fun initialize() {
        if(initialized.compareAndSet(false, true)){
            currentState = getState(initialState)
            currentState.owner = this@StateMachine
            path.add(0, this)
            currentState.addParent(this)
            descendantStates.add(currentState)
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
            descendantStates.add(this)
            descendantStates.addAll(this.getDescendantStates())
        }

        states.add(state)
        return this
    }

    fun addState(state:State):StateMachine {

        state.owner = this@StateMachine
        state.addParent(this@StateMachine)
        descendantStates.add(state)
        descendantStates.addAll(state.getDescendantStates())
        states.add(state)
        descendantStates.add(state)
        return this
    }

    /**
     * 通过状态名称获取状态
     */
    private fun getState(stateType: BaseState): State = states.firstOrNull { stateType.javaClass == it.name.javaClass } ?: throw NoSuchElementException("$stateType is not in statemachine:$name")

    @Synchronized
    fun getCurrentState(): State? = if (isCurrentStateInitialized()) this.currentState else null

    private fun isCurrentStateInitialized() = ::currentState.isInitialized

    /**
     * 发送消息，驱动状态的转换
     */
    @Synchronized
    fun sendEvent(event: BaseEvent): Boolean = if (isCurrentStateInitialized()) currentState.processEvent(event) else false

    internal fun executeTransition(transition: Transition, event: BaseEvent) {
        val stateContext: StateContext = DefaultStateContext(event, transition, transition.getSourceState(), transition.getTargetState())
        when (transition.getTransitionType()) {
            TransitionType.External -> doExternalTransition(stateContext)
            TransitionType.Local    -> doLocalTransition(stateContext)
            TransitionType.Internal -> executeAction(stateContext)
        }
    }

    private fun doExternalTransition(stateContext: StateContext) {
        val targetState = getState(stateContext.getTarget())
        val lowestCommonAncestor: StateMachine = findLowestCommonAncestor(targetState)
        lowestCommonAncestor.switchState(stateContext)
    }

    private fun doLocalTransition(stateContext: StateContext) {
        val previousState = getState(stateContext.getSource())
        val targetState = getState(stateContext.getTarget())

        when {
            previousState.getDescendantStates().contains(targetState) -> {
                val stateMachine = findNextStateMachineOnPathTo(targetState)
                stateMachine.switchState(stateContext)
            }
            targetState.getDescendantStates().contains(previousState) -> {
                val targetLevel = targetState.owner!!.path.size
                val stateMachine = path[targetLevel]
                stateMachine.switchState(stateContext)
            }
            previousState == targetState -> {
                executeAction(stateContext)
            }
            else -> doExternalTransition(stateContext)
        }
    }

    private fun findLowestCommonAncestor(targetState: State): StateMachine {
        checkNotNull(targetState.owner) { "$targetState is not contained in state machine model." }
        val targetPath = targetState.owner!!.path

        (1..targetPath.size).forEach { index ->
            try {
                val targetAncestor = targetPath[index]
                val localAncestor = path[index]
                if (targetAncestor != localAncestor) {
                    return path[index - 1]
                }
            } catch (e: IndexOutOfBoundsException) {
                return path[index - 1]
            }
        }
        return this
    }

    /**
     * 状态切换
     */
    private fun switchState(stateContext: StateContext) {
        try {
            val guard = stateContext.getTransition().getGuard()?.invoke()?:true

            if (guard) {
                globalInterceptor?.transitionStarted(stateContext.getTransition())

                exitState(stateContext)
                executeAction(stateContext)

                interceptors.forEach { interceptor -> interceptor.enteringState(this, stateContext) }

                enterState(stateContext)

                interceptors.forEach { interceptor -> interceptor.enteredState(this, stateContext) }
            } else {
                println("${stateContext.getTransition()} 失败")
                globalInterceptor?.stateMachineError(this, StateMachineException("状态转换失败: guard [${guard}], 状态 [${currentState.name}]，事件 [${stateContext.getEvent().javaClass.simpleName}]"))
            }
        } catch (exception:Exception) {
            globalInterceptor?.stateMachineError(this, StateMachineException("This state [${this.currentState.name}] doesn't support transition on ${stateContext.getEvent().javaClass.simpleName}"))
        }
    }

    private fun exitState(stateContext: StateContext) {
        currentState.exit()

        globalInterceptor?.apply {
            stateContext(stateContext)
            transition(stateContext.getTransition())
            stateExited(currentState)
        }
    }

    private fun executeAction(stateContext: StateContext) {
        val transition = stateContext.getTransition()
        transition.transit(stateContext)
    }

    private fun enterState(stateContext: StateContext) {
        val sourceState = getState(stateContext.getSource())
        val targetState = getState(stateContext.getTarget())
        val targetLevel = targetState.owner!!.path.size
        val localLevel = path.size
        val nextState: State = when {
            targetLevel < localLevel -> getState(initialState)
            targetLevel == localLevel -> targetState
            // targetLevel > localLevel
            else -> findNextStateOnPathTo(targetState)
        }

        currentState = if (states.contains(nextState)) {
            nextState
        } else {
            getState(initialState)
        }

        currentState.enter()

        globalInterceptor?.apply {
            stateEntered(currentState)
            stateChanged(sourceState,currentState)
            transitionEnded(stateContext.getTransition())
        }
    }

    private fun findNextStateOnPathTo(targetState: State): State = findNextStateMachineOnPathTo(targetState).container

    private fun findNextStateMachineOnPathTo(targetState: State): StateMachine {
        val localLevel = path.size
        val targetOwner = targetState.owner!!
        return targetOwner.path[localLevel]
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
     * 注册 Interceptor
     */
    fun registerInterceptor(interceptor: Interceptor) = interceptors.add(interceptor)

    /**
     * 取消 Interceptor
     */
    fun unregisterInterceptor(interceptor: Interceptor) = interceptors.remove(interceptor)

    companion object {
        /**
         * @param name 状态机的名称
         * @param initialStateName 初始化状态机的 block
         */
        fun buildStateMachine(name:String = "StateMachine", initialStateName: BaseState, init: StateMachine.() -> Unit): StateMachine  =
            StateMachine(name,initialStateName).apply(init)
    }
}