package com.safframework.statemachine.transition

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.TransitionType
 * @author: Tony Shen
 * @date: 2020-02-29 18:04
 * @version: V1.0 <描述当前版本功能>
 */
enum class TransitionType {
    External,  // 当source和target不同时
    Local,     // 类似于 External，但是不会跳出 Composite state
    Internal   // 当source和target相同时
}