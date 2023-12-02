package net.zhuruoling.nekomemo.client.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

internal actual val client: HttpClient by lazy {
    HttpClient(OkHttp) {
        install(ContentNegotiation)
    }
}


