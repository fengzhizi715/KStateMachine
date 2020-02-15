package com.safframework.statemachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.Base
 * @author: Tony Shen
 * @date: 2020-02-14 21:38
 * @version: V1.0 <描述当前版本功能>
 */
abstract class BaseEvent : Base()

abstract class BaseState : Base()

abstract class Base {

    override fun equals(other: Any?): Boolean {
        val e = other as Base
        return this.javaClass == e.javaClass
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = this.javaClass.simpleName
}