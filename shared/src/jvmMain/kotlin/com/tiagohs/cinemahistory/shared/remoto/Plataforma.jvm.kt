package com.tiagohs.cinemahistory.shared.remoto

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okio.FileSystem

actual val sistemaDeArquivos: FileSystem = FileSystem.SYSTEM

actual fun criarClienteHttp(): HttpClient = HttpClient(OkHttp)
