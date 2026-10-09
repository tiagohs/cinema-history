package com.tiagohs.cinema_history.audio

import android.content.Context
import android.os.SystemClock
import com.tiagohs.helpers.utils.ServerUtils
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Descobre se um capítulo tem áudio (baixando o manifest.json) e guarda o resultado.
 *
 * - Cache em memória: positivo por 6 h, negativo (404/erro) por 10 min, para não pedir de novo a cada página.
 * - Sem rede: só aparece o áudio dos capítulos baixados para ouvir offline (manifest salvo em disco);
 *   os demais ficam escondidos.
 */
object AudioRepository {

    private const val POSITIVE_TTL_MS = 6 * 60 * 60 * 1000L
    private const val NEGATIVE_TTL_MS = 10 * 60 * 1000L

    private class Entry(val manifest: AudioManifest?, val at: Long)

    private val memory = ConcurrentHashMap<String, Entry>()

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private fun cacheKey(context: Context, key: ChapterKey) = AudioConfig.baseUrl(context) + "|" + key.id

    fun clearMemory() = memory.clear()

    /** Resultado já conhecido (sem rede). */
    fun cached(context: Context, key: ChapterKey): AudioManifest? =
        memory[cacheKey(context, key)]?.manifest ?: readOffline(context, key)

    /** Manifest do capítulo, ou vazio se não houver áudio. Resultado na main thread. */
    fun manifest(context: Context, key: ChapterKey): Maybe<AudioManifest> {
        val app = context.applicationContext
        return Maybe.fromCallable<AudioManifest> { load(app, key) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /** Bloqueante (use fora da main thread). */
    fun load(context: Context, key: ChapterKey): AudioManifest? {
        val ck = cacheKey(context, key)
        val now = SystemClock.elapsedRealtime()
        memory[ck]?.let { e ->
            val ttl = if (e.manifest != null) POSITIVE_TTL_MS else NEGATIVE_TTL_MS
            if (now - e.at < ttl) return e.manifest
        }
        val dir = AudioConfig.chapterDir(context, key)
        val result: AudioManifest? = if (AudioConfig.isTestSource(context)) {
            loadAsset(context, key, dir)
        } else {
            val offline = readOffline(context, key)
            if (!ServerUtils.isNetworkConnected(context)) {
                // sem rede: não guarda o negativo, para tentar de novo quando a conexão voltar
                return offline
            }
            try {
                fetch(context, key, dir) ?: offline
            } catch (e: Exception) {
                Timber.d(e, "audio manifest %s", key.id)
                return offline
            }
        }
        memory[ck] = Entry(result, now)
        return result
    }

    private fun fetch(context: Context, key: ChapterKey, dir: String): AudioManifest? {
        val request = Request.Builder().url(AudioConfig.manifestUrl(context, key)).build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val manifest = AudioManifest.parse(body, key, dir)
            // capítulo baixado: mantém o manifest em disco atualizado
            if (AudioDownloads.isDownloaded(context, key)) saveOffline(context, key, body)
            return manifest
        }
    }

    private fun loadAsset(context: Context, key: ChapterKey, dir: String): AudioManifest? = try {
        val path = "audio-test/${key.lang}/main_${key.era}/page_${key.page}/manifest.json"
        val json = context.assets.open(path).bufferedReader().use { it.readText() }
        AudioManifest.parse(json, key, dir)
    } catch (e: Exception) {
        null
    }

    // --- cópia em disco (capítulos baixados) --------------------------------------------------

    private fun offlineFile(context: Context, key: ChapterKey) =
        File(context.filesDir, "audio/manifests/${key.lang}_${key.era}_${key.page}.json")

    internal fun saveOffline(context: Context, key: ChapterKey, json: String) {
        try {
            val f = offlineFile(context, key)
            f.parentFile?.mkdirs()
            f.writeText(json)
        } catch (e: Exception) {
            Timber.w(e, "audio manifest offline %s", key.id)
        }
    }

    internal fun deleteOffline(context: Context, key: ChapterKey) {
        offlineFile(context, key).delete()
    }

    private fun readOffline(context: Context, key: ChapterKey): AudioManifest? {
        if (AudioConfig.isTestSource(context) || !AudioDownloads.isDownloaded(context, key)) return null
        val f = offlineFile(context, key)
        if (!f.exists()) return null
        return try {
            AudioManifest.parse(f.readText(), key, AudioConfig.chapterDir(context, key))
        } catch (e: Exception) {
            null
        }
    }
}
