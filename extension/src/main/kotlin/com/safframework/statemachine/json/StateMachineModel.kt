package com.safframework.statemachine.json


/**
 *
 * @FileName:
 *          com.safframework.statemachine.json.StateMachineModel
 * @author: Tony Shen
 * @date: 2023/9/21 16:52
 * @version: V1.0 <描述当前版本功能>
 */
data class TransitionModel(
    val name: String?="",
    val sourceState:String,
    val targetState:String,
    val event:String,
    val type:String
)

data class StateModel(
    val name: String?="",
    val isInit:Boolean = false,
    val isFinal:Boolean = false,
    val transitions: List<TransitionModel>,
    val childMode: String?=null,
    val subStates:List<StateModel>?=null
)

data class StateMachineModel(
    val machineName: String?="",
    val states: List<StateModel>
)