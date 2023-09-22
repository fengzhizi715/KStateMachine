package com.safframework.statemachine.json

import com.google.gson.Gson
import java.lang.reflect.Type


/**
 *
 * @FileName:
 *          com.safframework.statemachine.json.GsonUtils
 * @author: Tony Shen
 * @date: 2023/9/21 19:34
 * @version: V1.0 <描述当前版本功能>
 */

object GsonUtils {
    private val gson = Gson()

    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    fun toJson(data: Any): String {
        return gson.toJson(data)
    }
}