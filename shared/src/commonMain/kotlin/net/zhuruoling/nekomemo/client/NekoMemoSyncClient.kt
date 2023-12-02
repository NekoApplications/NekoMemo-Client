package net.zhuruoling.nekomemo.client

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.decodeBase64String
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.readBytes
import net.zhuruoling.nekomemo.client.data.ContentType
import net.zhuruoling.nekomemo.client.data.HttpResponse
import net.zhuruoling.nekomemo.client.data.Responses
import net.zhuruoling.nekomemo.client.data.SessionData
import net.zhuruoling.nekomemo.client.data.SessionDataResponse
import net.zhuruoling.nekomemo.client.data.SessionStatus
import net.zhuruoling.nekomemo.client.network.client
import net.zhuruoling.nekomemo.client.network.httpGet
import net.zhuruoling.nekomemo.client.network.httpPost
import net.zhuruoling.nekomemo.client.util.Either
import net.zhuruoling.nekomemo.client.util.SessionKeyStore
import kotlin.properties.Delegates

class NekoMemoSyncClient(private val serverAddress: String) {
    private var sessionData = SessionData(serverAddress)
    private var keyStore: SessionKeyStore by Delegates.notNull()

    private suspend fun httpGet(path: String): String {
        return httpGet(sessionData, path)
    }

    private suspend fun <T> httpGetStream(
        path: String,
        block: suspend ByteReadChannel.() -> T
    ): T {
        return net.zhuruoling.nekomemo.client.network.httpGetStream(sessionData, path, block)
    }

    private suspend fun httpPost(
        path: String,
        body: ByteArray
    ): String {
        return httpPost(sessionData, path, body)
    }

    private suspend fun httpDelete(
        path: String
    ): String {
        return net.zhuruoling.nekomemo.client.network.httpDelete(sessionData, path)
    }

    suspend fun validateSessionId0(): SessionStatus {
        sessionData.sessionId ?: return SessionStatus.UNAVAILABLE
        val validateResult = httpGet("/session/validate").toObject(HttpResponse::class)
        sessionData.sessionStatus = when (validateResult.code) {
            Responses.SESSION_EXPIRED -> SessionStatus.EXPIRED
            Responses.SESSION_VALIDATED -> SessionStatus.AVAILABLE
            else -> SessionStatus.UNAVAILABLE
        }
        return sessionData.sessionStatus
    }

    suspend fun authByAccessToken0(token: String): Pair<SessionStatus, Responses> {
        sessionData.accessToken = token
        val authResult =
            httpPost("/session/auth", sessionData.accessToken.toString().encodeToByteArray()).toObject(HttpResponse::class)
        sessionData.sessionStatus = when (authResult.code) {
            Responses.ACCESS_TOKEN_VERIFIED -> {
                require(authResult.contentType == ContentType.SESSION)
                val session =
                    ContentType.SESSION.contentSerializer.deserialize(authResult.content) as SessionDataResponse
                sessionData.sessionId = session.sessionId
                keyStore = SessionKeyStore(session.publicKey.decodeBase64Bytes())
                SessionStatus.AVAILABLE
            }

            Responses.ACCESS_TOKEN_MISMATCH -> {
                SessionStatus.UNAVAILABLE
            }

            else -> SessionStatus.UNAVAILABLE
        }
        return sessionData.sessionStatus to authResult.code
    }

    suspend fun deactivateSession0(): SessionStatus {
        if (sessionData.sessionId == null) return SessionStatus.UNAVAILABLE
        val result = httpGet("/session/deactivate").toObject(HttpResponse::class)
        sessionData.sessionStatus = when (result.code) {
            Responses.SESSION_DEACTIVATED -> {
                sessionData.sessionId = null
                SessionStatus.UNAVAILABLE
            }

            Responses.SESSION_EXPIRED -> {
                SessionStatus.EXPIRED
            }

            Responses.SESSION_NOT_EXIST -> {
                SessionStatus.UNAVAILABLE
            }

            else -> {
                sessionData.sessionStatus
            }
        }
        return sessionData.sessionStatus
    }

    suspend fun refreshSession0(token: String): Pair<SessionStatus, Responses> {
        deactivateSession0()
        return authByAccessToken0(token)
    }

    suspend fun listFile(): Pair<List<String>, Responses> {
        val result = httpGet("/file/list").toObject(HttpResponse::class)
        return when (result.code) {
            Responses.FILE_LIST -> {
                require(result.contentType == ContentType.STRING_ARRAY)
                val arr =
                    ContentType.STRING_ARRAY.contentSerializer.deserialize(result.content.decodeBase64String()) as Array<String>
                arr.toMutableList()
            }

            Responses.SESSION_EXPIRED -> {
                sessionData.sessionStatus = SessionStatus.EXPIRED
                listOf()
            }

            Responses.SESSION_NOT_EXIST -> {
                sessionData.sessionStatus = SessionStatus.UNAVAILABLE
                listOf()
            }

            else -> {
                listOf()
            }
        } to result.code
    }

    suspend fun retrieveFileStream0(
        fileName: String
    ): Either<Responses, ByteArray> {
        val response =
            client.get("${sessionData.getHttpPrefix()}://${sessionData.serverAddress}/file/fetch?name=$fileName") {
                headers {
                    append("Session-Id", sessionData.sessionId.toString())
                }
            }
        if (response.contentType() == io.ktor.http.ContentType.Application.Json) {
            val resp = response.bodyAsText(Charsets.UTF_8).toObject(HttpResponse::class)
            when (resp.code) {
                Responses.SESSION_EXPIRED -> {
                    sessionData.sessionStatus = SessionStatus.EXPIRED
                }

                Responses.SESSION_NOT_EXIST -> {
                    sessionData.sessionStatus = SessionStatus.UNAVAILABLE
                }

                else -> {}
            }
            return Either.left(resp.code)
        } else {
            val channel = response.bodyAsChannel()
            val packet = channel.readRemaining()
            return Either.right(packet.readBytes())
        }
    }

    suspend fun updateFile0(
        replace: Boolean = false,
        name: String,
        content: ByteArray
    ): Responses {
        val resp = httpPost(
            "/file/update?name=$name&replace=$replace",
            content
        ).toObject(HttpResponse::class)
        when (resp.code) {
            Responses.SESSION_EXPIRED -> {
                sessionData.sessionStatus = SessionStatus.EXPIRED
            }

            Responses.SESSION_NOT_EXIST -> {
                sessionData.sessionStatus = SessionStatus.UNAVAILABLE
            }
            else -> {}
        }
        return resp.code
    }

    suspend fun deleteFile0(name: String): Responses {
        val resp = httpDelete("/file/delete?name=$name").toObject(HttpResponse::class)
        when (resp.code) {
            Responses.SESSION_EXPIRED -> {
                sessionData.sessionStatus = SessionStatus.EXPIRED
            }

            Responses.SESSION_NOT_EXIST -> {
                sessionData.sessionStatus = SessionStatus.UNAVAILABLE
            }
            else -> {}
        }
        return resp.code
    }
}