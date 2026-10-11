package com.tiagohs.cinemahistory.shared.remoto

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import okio.FileSystem

actual val sistemaDeArquivos: FileSystem = FileSystem.SYSTEM

actual fun criarClienteHttp(): HttpClient = HttpClient(Darwin) {
    engine {
        configureRequest {
            setTimeoutInterval(30.0)
        }
    }
}
