package com.tiagohs.cinema_history.services.config

import android.content.Context
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.enums.LocalFiles
import com.tiagohs.cinema_history.helpers.utils.FileUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.io.IOException


class FakeInterceptor : Interceptor {

    private var context: Context? = null
    private var content: String = ""

    init {
        context = App.appContext
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = this.content.toByteArray().toResponseBody(mediaType)

        try {
            setup(chain.request())

            response = Response.Builder()
                    .code(200)
                    .message(this.content)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create("application/json".toMediaTypeOrNull(), this.content.toByteArray()))
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

        for (stub in LocalFiles.values()) {
            if (stub.isValid(request)) {
                raw = stub.raw
                break
            }
        }

        if (raw.isNotBlank()) {
            content = readFileFromStatus(raw)
        }
    }

    @Throws(IOException::class)
    private fun readFileFromStatus(rawFileName: String): String {
        return context?.let {
            FileUtils.readAssetsFile(it, rawFileName, "UTF-8")
        } ?: ""
    }

}