package com.tiagohs.cinema_history.helpers.utils

import android.content.Context
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object FileUtils {

    @Throws(IOException::class)
    fun readAssetsFile(context: Context, fileName: String, charset: String): String {
        val assets = context.assets
        val `in` = assets.open(fileName)
        return toString(`in`, charset)
    }

    @Throws(IOException::class)
    fun toString(`in`: InputStream, charset: String): String {
        val bytes = toBytes(`in`)
        return String(bytes!!, Charset.forName(charset) )
    }

    private fun toBytes(`in`: InputStream): ByteArray? {
        val bos = ByteArrayOutputStream()
        try {
            val buffer = ByteArray(1024)
            var len: Int = `in`.read(buffer)
            while (len > 0) {
                bos.write(buffer, 0, len)
                len = `in`.read(buffer)
            }
            return bos.toByteArray()
        } catch (e: Exception) {
            Timber.e(e)
            return null
        } finally {
            try {
                bos.close()
                `in`.close()
            } catch (e: IOException) {
                Timber.e(e)
            }

        }
    }
}