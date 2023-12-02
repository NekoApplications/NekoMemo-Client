package net.zhuruoling.nekomemo.client

import net.zhuruoling.nekomemo.client.data.SessionDataResponse
import kotlin.reflect.KClass


expect inline fun <reified T : Any> T.toJson():String

expect inline fun <reified T : Any> String.toObject(clazz: KClass<T>): T