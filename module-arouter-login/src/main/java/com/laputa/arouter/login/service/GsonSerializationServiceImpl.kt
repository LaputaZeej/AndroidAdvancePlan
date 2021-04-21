package com.laputa.arouter.login.service

import android.content.Context
import com.alibaba.android.arouter.facade.service.SerializationService
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Author by xpl, Date on 2021/4/19.
 */
class GsonSerializationServiceImpl: SerializationService {
    private lateinit var gson: Gson
    override fun init(context: Context?) {
        gson = Gson()
    }

    override fun <T : Any?> json2Object(input: String?, clazz: Class<T>?): T {
        return gson.fromJson(input,clazz)
    }

    override fun object2Json(instance: Any?): String {
        return gson.toJson(instance)
    }

    override fun <T : Any?> parseObject(input: String?, clazz: Type?): T {
        return gson.fromJson(input,clazz)
    }
}