package com.safframework.statemachine.json

import com.safframework.statemachine.state.ChildMode
import com.safframework.statemachine.statemachine.StateMachine
import com.safframework.statemachine.statemachine.createStateMachine

/**
 *
 * @FileName:
 *          com.safframework.statemachine.json.`StateMachine+Extension`
 * @author: Tony Shen
 * @date: 2023/9/21 20:23
 * @version: V1.0 <描述当前版本功能>
 */
fun StateMachine.createStateMachineFromJSON(
    childMode: ChildMode = ChildMode.EXCLUSIVE,
    start: Boolean = true,
    json: String
):StateMachine? {
    val model:StateMachineModel? = try {
        GsonUtils.fromJson<StateMachineModel>(json, StateMachineModel::class.java)
    } catch (e: Exception) {
        return null
    }

    val stateMachine = createStateMachine(model?.machineName,childMode,start) {
    }

    return stateMachine
}