package com.tiagohs.helpers.network

import android.content.Context
import com.tiagohs.entities.enums.LocalFiles
import com.tiagohs.helpers.utils.ContentLanguage
import com.tiagohs.helpers.utils.FileUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException


class FakeInterceptor(
    val context: Context?
) : Interceptor {

    private var content: String = ""

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response

        try {
            setup(chain.request())

            response = Response.Builder()
                    .code(200)
                    .message(this.content)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(this.content.toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                    .addHeader("content-type", "application/json")
                    .build()

            return response
        } catch (e: NullPointerException) {
            Timber.e("Fake Interceptor Error - %s", chain.request().url.toUri().path)

            return chain.proceed(chain.request())
        } catch (e: IOException) {
            Timber.e("Fake Interceptor Error")

            return chain.proceed(chain.request())
        }
    }

    @Throws(IOException::class)
    private fun setup(request: Request) {
        var raw = ""

        for (stub in com.tiagohs.entities.enums.LocalFiles.values()) {
            if (stub.isValid(request)) {
                raw = stub.raw
                break
            }
        }

        if (raw.isNotBlank()) {
            content = readFileFromStatus(raw)
        }
    }

    /**
     * Lê o conteúdo no idioma atual (assets/local/<idioma>/...).
     * Se o arquivo não existir nesse idioma, usa o português (idioma de origem).
     */
    @Throws(IOException::class)
    private fun readFileFromStatus(rawFileName: String): String {
        val context = context ?: return ""
        val language = ContentLanguage.current()

        return try {
            FileUtils.readAssetsFile(context, ContentLanguage.assetPath(rawFileName, language), "UTF-8")
        } catch (e: FileNotFoundException) {
            if (language == ContentLanguage.SOURCE) throw e

            Timber.w("Conteúdo sem tradução para '%s', usando '%s': %s", language, ContentLanguage.SOURCE, rawFileName)
            FileUtils.readAssetsFile(context, ContentLanguage.assetPath(rawFileName, ContentLanguage.SOURCE), "UTF-8")
        }
    }

}