package net.zhuruoling.nekomemo.client

import com.google.gson.GsonBuilder
import kotlin.reflect.KClass

val gson by lazy { GsonBuilder().serializeNulls().create() }

actual inline fun <reified T : Any> T.toJson():String {
    return gson.toJson(this)
}

actual inline fun <reified T : Any> String.toObject(clazz: KClass<T>): T{
    return gson.fromJson(this, clazz.java)
}