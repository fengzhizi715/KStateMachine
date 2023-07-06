package com.safframework.statemachine.v2.transition

import com.safframework.statemachine.v2.state.IState

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.transition.TransitionDirection
 * @author: Tony Shen
 * @date: 2023/7/4 10:03
 * @version: V1.0 <描述当前版本功能>
 */
sealed class TransitionDirection {
    open val targetState: IState? = null

    override fun toString():String {
        return "targetState{$targetState}"
    }
}

internal object Stay : TransitionDirection()

fun stay(): TransitionDirection = Stay

internal class TargetState(override val targetState: IState) : TransitionDirection()

fun targetState(targetState: IState): TransitionDirection = TargetState(targetState)

internal object NoTransition : TransitionDirection()

fun noTransition(): TransitionDirection = NoTransition