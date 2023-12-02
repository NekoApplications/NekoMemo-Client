package net.zhuruoling.nekomemo.client.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation


internal actual val client: HttpClient by lazy {
    HttpClient(CIO) {
        install(ContentNegotiation)
    }
}