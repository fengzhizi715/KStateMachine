package com.safframework.statemachine.transition

import com.safframework.statemachine.domain.Event
import kotlin.reflect.KClass

/**
 *
 * @FileName:
 *          com.safframework.statemachine.transition.EventMatcher
 * @author: Tony Shen
 * @date: 2023/7/4 11:52
 * @version: V1.0 <描述当前版本功能>
 */
abstract class EventMatcher<E : Event>(val eventClass: KClass<E>) {
    abstract fun match(value: Event): Boolean

    companion object {
        /** This matcher is used by default, allowing [Event] subclasses */
        inline fun <reified E : Event> isInstanceOf() = object : EventMatcher<E>(E::class) {
            override fun match(value: Event) = value is E
        }
    }
}