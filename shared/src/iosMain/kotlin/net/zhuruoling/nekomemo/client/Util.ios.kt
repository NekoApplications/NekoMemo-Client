package net.zhuruoling.nekomemo.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

val json by lazy {
    Json{
        prettyPrint = false
    }
}

actual inline fun <reified T : Any> T.toJson():String {
    return json.encodeToString(this)
}

actual inline fun <reified T : Any> String.toObject(clazz: KClass<T>): T {
    return json.decodeFromString<T>(this)
}