package net.zhuruoling.nekomemo.client.data

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val name: String, val message: String, val stackTraceElements: List<String>)

@Serializable
data class HttpResponse(val code: Responses, val error: ErrorResponse? = null, val contentType: ContentType = ContentType.EMPTY, val content: String = "")

@Serializable
data class SessionDataResponse(val sessionId: String, val publicKey: String){}