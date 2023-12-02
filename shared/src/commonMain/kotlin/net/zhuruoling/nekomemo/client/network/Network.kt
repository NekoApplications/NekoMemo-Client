package net.zhuruoling.nekomemo.client.network

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.runBlocking
import net.zhuruoling.nekomemo.client.data.SessionData

internal expect val client: HttpClient

suspend fun httpGet(sessionData: SessionData, path: String): String {
    return client.get(sessionData, path).bodyAsText(Charsets.UTF_8)
}

suspend fun <T> httpGetStream(
    sessionData: SessionData,
    path: String,
    block: suspend ByteReadChannel.() -> T
):T {
   return client.get(sessionData, path).bodyAsChannel().block()
}

suspend fun httpPost(
    sessionData: SessionData,
    path: String,
    body: ByteArray
): String {
    return client.post("${sessionData.getHttpPrefix()}://${sessionData.serverAddress}$path") {
        headers {
            append("Session-Id", sessionData.sessionId.toString())
        }
        setBody(ByteArrayContent(body))
    }.bodyAsText(Charsets.UTF_8)
}

internal suspend fun HttpClient.get(sessionData: SessionData, path: String): HttpResponse {
    return get("${sessionData.getHttpPrefix()}://${sessionData.serverAddress}$path") {
        headers {
            append("Session-Id", sessionData.sessionId.toString())
        }
    }
}

internal suspend fun HttpClient.delete(sessionData: SessionData, path: String): HttpResponse {
    return delete("${sessionData.getHttpPrefix()}://${sessionData.serverAddress}$path") {
        headers {
            append("Session-Id", sessionData.sessionId.toString())
        }
    }
}

suspend fun httpDelete(
    sessionData: SessionData,
    path: String
): String {
    return client.delete(sessionData, path).bodyAsText(Charsets.UTF_8)
}