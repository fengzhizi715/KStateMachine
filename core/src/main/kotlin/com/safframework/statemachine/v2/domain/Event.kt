package com.safframework.statemachine.v2.domain

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.domain.Event
 * @author: Tony Shen
 * @date: 2023/7/3 20:44
 * @version: V1.0 <描述当前版本功能>
 */
interface Event

interface DataEvent<out D> : Event {
    val data: D
}

/**
 * Initial event which is processed on state machine start
 */
internal object StartEvent : Event