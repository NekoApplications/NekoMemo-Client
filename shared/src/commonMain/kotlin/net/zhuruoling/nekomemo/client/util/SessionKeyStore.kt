package net.zhuruoling.nekomemo.client.util

class SessionKeyStore(
    val serverPublicKey: ByteArray,
    var clientPublicKey: ByteArray = ByteArray(0),
    var clientPrivateKey: ByteArray = ByteArray(0),
    var sentPublicKeyToServer: Boolean = false
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SessionKeyStore

        if (!serverPublicKey.contentEquals(other.serverPublicKey)) return false
        if (!clientPublicKey.contentEquals(other.clientPublicKey)) return false
        if (!clientPrivateKey.contentEquals(other.clientPrivateKey)) return false
        if (sentPublicKeyToServer != other.sentPublicKeyToServer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverPublicKey.contentHashCode()
        result = 31 * result + clientPublicKey.contentHashCode()
        result = 31 * result + clientPrivateKey.contentHashCode()
        result = 31 * result + sentPublicKeyToServer.hashCode()
        return result
    }
}