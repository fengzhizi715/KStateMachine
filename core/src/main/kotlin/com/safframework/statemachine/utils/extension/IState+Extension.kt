package com.safframework.statemachine.utils.extension

import com.safframework.statemachine.StateBlock
import com.safframework.statemachine.interceptor.Interceptor
import com.safframework.statemachine.transition.TransitionParams
import com.safframework.statemachine.state.*
import kotlin.reflect.KClass

/**
 *
 * @FileName:
 *          com.safframework.statemachine.utils.extension.`IState+Extension`
 * @author: Tony Shen
 * @date: 2023/7/4 10:22
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * Get state by name. This might be used to start listening to state after state machine setup.
 */
fun IState.findState(name: String, recursive: Boolean = true): IState? {
    val result = states.find { it.name == name }

    if (!recursive || result != null)
        return result

    return states.firstNotNullOfOrNull { it.findState(name, recursive) }
}

fun IState.requireState(name: String, recursive: Boolean = true) =
    requireNotNull(findState(name, recursive)) { "State $name not found" }

/**
 * Find state by type. Search by type is suitable when using own state subclasses that usually do not have a name.
 * Only on state should match the type or exception will be thrown.
 */
inline fun <reified S : IState> IState.findState(recursive: Boolean = true) = findState(S::class, recursive)

/**
 * For internal use. Workaround that Kotlin does not support recursive inline functions.
 */
@Suppress("UNCHECKED_CAST")
fun <S : IState> IState.findState(`class`: KClass<S>, recursive: Boolean = true): S? {
    fun requireSingleOrEmpty(collection: Collection<*>) = require(collection.size <= 1) {
        "More than one state matches ${`class`.simpleName}"
    }

    val filtered = states.filter { `class`.isInstance(it) }
    requireSingleOrEmpty(filtered)

    if (!recursive) return filtered.singleOrNull() as S?

    val nestedFiltered = states.mapNotNull { it.findState(`class`, recursive) }
    requireSingleOrEmpty(nestedFiltered)

    val allFiltered = filtered + nestedFiltered
    requireSingleOrEmpty(allFiltered)

    return allFiltered.singleOrNull() as S?
}

/**
 * Require state by type
 */
inline fun <reified S : IState> IState.requireState(recursive: Boolean = true) =
    requireNotNull(findState<S>(recursive)) { "State ${S::class.simpleName} not found" }

operator fun <S : IState> S.invoke(block: StateBlock<S>) = block()

fun <S : IState> S.entry(block: S.(TransitionParams<*>) -> Unit) {
    addInterceptor(object : Interceptor {
        override fun onEntry(transitionParams: TransitionParams<*>) = block(transitionParams)
    })
}

fun <S : IState> S.exit(block: S.(TransitionParams<*>) -> Unit) {
    addInterceptor(object : Interceptor {
        override fun onExit(transitionParams: TransitionParams<*>) = block(transitionParams)
    })
}

fun <S : IState> S.finished(block: S.(TransitionParams<*>) -> Unit) {
    addInterceptor(object : Interceptor {
        override fun onFinished(transitionParams: TransitionParams<*>) = block(transitionParams)
    })
}

/**
 * @param name is optional and is useful for getting state instance after state machine setup
 * with [IState.findState] and for debugging.
 */
fun IState.state(
    name: String? = null,
    childMode: ChildMode = ChildMode.EXCLUSIVE,
    init: StateBlock<State>? = null
) = addState(DefaultState(name, childMode), init)

fun <D> IState.dataState(
    name: String? = null,
    childMode: ChildMode = ChildMode.EXCLUSIVE,
    init: StateBlock<DataState<D>>? = null
) = addState(DefaultDataState(name, childMode), init)

/**
 * A shortcut for [state] and [IState.setInitialState] calls
 */
fun IState.initialState(
    name: String? = null,
    childMode: ChildMode = ChildMode.EXCLUSIVE,
    init: StateBlock<State>? = null
) = addInitialState(DefaultState(name, childMode), init)

/**
 * A shortcut for [IState.addState] and [IState.setInitialState] calls
 */
fun <S : IState> IState.addInitialState(state: S, init: StateBlock<S>? = null): S {
    addState(state, init)
    setInitialState(state)
    return state
}

/**
 * Helper method for adding final states. This is exactly the same as simply call [IState.addState] but makes
 * code more self expressive.
 */
fun <S : IFinalState> IState.addFinalState(state: S, init: StateBlock<S>? = null) =
    addState(state, init)

fun IState.finalState(name: String? = null, init: StateBlock<FinalState>? = null) =
    addFinalState(DefaultFinalState(name), init)

fun <D> IState.finalDataState(name: String? = null, init: StateBlock<FinalDataState<D>>? = null) =
    addFinalState(DefaultFinalDataState(name), init)

fun IState.isFinal() = this is IFinalState



