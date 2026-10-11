package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.data.ContentSource
import java.io.File

/** Lê o conteúdo real do app Android, a mesma pasta que o Xcode embute no iOS (uma fonte só). */
class ConteudoDoAndroid : ContentSource {
    val raiz: File = generateSequence(File("").absoluteFile) { it.parentFile }
        .map { File(it, "android/app/src/main/assets/local") }
        .first { it.isDirectory }

    override fun lerTexto(caminho: String): String? = File(raiz, caminho).takeIf { it.isFile }?.readText()
}
