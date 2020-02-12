package com.safframework.statemachine.action

import com.safframework.statemachine.context.StateContext

/**
 * 当一个 Event 被状态机系统分发的时候，状态机用 Action 来进行响应
 *
 * Created by tony on 2019/12/21.
 */
interface Action<S, E> {

    /**
     * 执行动作，状态机不保证幂等，请在execute方法内自己实现业务幂等
     * 如果action成功返回则代表事件触发成功，否则请抛出异常
     * @param context 上下文
     */
    fun execute(context: StateContext<S, E>)
}