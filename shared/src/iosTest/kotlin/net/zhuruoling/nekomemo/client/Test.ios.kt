package net.zhuruoling.nekomemo.client

import kotlinx.serialization.json.Json
import net.zhuruoling.nekomemo.client.data.SessionDataResponse
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals


class IosTest {

    @OptIn(ExperimentalNativeApi::class)
    @Test
    fun testSerialization() {
        val expect = """{"sessionId":"hello","publicKey":"world"}"""
        val obj = SessionDataResponse("hello", "world")
        val j = obj.toJson()
        assertEquals(expect, j)
        val objFromJson = j.toObject(SessionDataResponse::class)
        assertEquals(obj, objFromJson)

        val arr = IntArray(3) { it }
        var result = arr.toJson()
        assertEquals("[0,1,2]", result)
        val arrFromJson = result.toObject(IntArray::class)
        assert(arrFromJson.contentEquals(arr))

        val stringArray = arrayOf("a","b")
        result = stringArray.toJson()
        assertEquals("""["a","b"]""", result)
        val arrayFromString = Json.decodeFromString<Array<String>>(result)
        assert(stringArray.contentEquals(arrayFromString))
    }
}