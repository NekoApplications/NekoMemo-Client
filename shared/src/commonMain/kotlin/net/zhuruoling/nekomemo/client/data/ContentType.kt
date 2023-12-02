package net.zhuruoling.nekomemo.client.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ContentSerializer<T>{
    fun deserialize(string: String):T
    fun serialize(obj:T):String
}

fun main() {
}

enum class ContentType(val contentSerializer: ContentSerializer<*>) {
    EMPTY(object :ContentSerializer<Unit>{
        override fun deserialize(string: String) {
            return Unit
        }

        override fun serialize(obj: Unit): String {
            return ""
        }

    }),
    RAW(object :ContentSerializer<String>{
        override fun deserialize(string: String): String {
            return string
        }

        override fun serialize(obj: String): String {
            return obj
        }

    }),
    STRING_ARRAY(object :ContentSerializer<Array<String>>{
        override fun deserialize(string: String): Array<String> {
            return Json.decodeFromString<Array<String>>(string)
        }

        override fun serialize(obj: Array<String>): String {
            return Json.encodeToString(obj)
        }

    }),
    SESSION(object : ContentSerializer<SessionDataResponse>{
        override fun deserialize(string: String): SessionDataResponse {
            return Json.decodeFromString<SessionDataResponse>(string)
        }

        override fun serialize(obj: SessionDataResponse): String {
            return Json.encodeToString(obj)
        }
    });
}