package net.zhuruoling.nekomemo.client.data

data class NoteItem(val id: String, val title: String, val content: String)

data class ReminderItem(val id: String, val content: String)

class SessionData(
    val serverAddress: String,
    var accessToken: String? = null,
    val enableHttps: Boolean = false,
    var sessionId: String? = null,
    var sessionStatus: SessionStatus = SessionStatus.UNAVAILABLE
) {
    fun getHttpPrefix(): String = if (enableHttps) "https" else "http"
}

enum class ValidateResult {
    PASS, EXPIRED, NOT_EXIST
}

enum class SessionStatus{
    EXPIRED,UNAVAILABLE,AVAILABLE
}