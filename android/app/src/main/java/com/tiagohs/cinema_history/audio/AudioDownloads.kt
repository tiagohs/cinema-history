package com.tiagohs.cinema_history.audio

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheWriter
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

/**
 * "Baixar para ouvir offline", por capítulo.
 *
 * Escolha: cache do Media3 (SimpleCache + CacheWriter) dentro de um Worker do WorkManager.
 * É o caminho mais simples e robusto aqui: não precisa de DownloadService/foreground service,
 * sobrevive ao app ir para o fundo, retoma de onde parou (o CacheWriter pula o que já está no cache)
 * e o player lê os mesmos arquivos pelo CacheDataSource, sem nenhuma troca de URL.
 */
object AudioDownloads {

    private const val PREFS = "audio_downloads"
    private const val KEY_SET = "chapters"

    private fun workName(key: ChapterKey) = "audio_download_${key.lang}_${key.era}_${key.page}"

    fun isDownloaded(context: Context, key: ChapterKey): Boolean =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY_SET, emptySet())?.contains(key.id) == true

    private fun setDownloaded(context: Context, key: ChapterKey, downloaded: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = HashSet(prefs.getStringSet(KEY_SET, emptySet()) ?: emptySet())
        if (downloaded) set += key.id else set -= key.id
        prefs.edit().putStringSet(KEY_SET, set).apply()
    }

    fun start(context: Context, key: ChapterKey) {
        val request = OneTimeWorkRequest.Builder(AudioDownloadWorker::class.java)
            .setInputData(Data.Builder().putString(AudioDownloadWorker.KEY_CHAPTER, key.id).build())
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(workName(key), ExistingWorkPolicy.KEEP, request)
    }

    fun observe(context: Context, key: ChapterKey): LiveData<List<WorkInfo>> =
        WorkManager.getInstance(context.applicationContext).getWorkInfosForUniqueWorkLiveData(workName(key))

    @OptIn(UnstableApi::class)
    fun remove(context: Context, manifest: AudioManifest) {
        val app = context.applicationContext
        WorkManager.getInstance(app).cancelUniqueWork(workName(manifest.key))
        setDownloaded(app, manifest.key, false)
        AudioRepository.deleteOffline(app, manifest.key)
        Thread({
            val cache = AudioCache.get(app)
            manifest.tracks.forEach { t ->
                try {
                    cache.removeResource(manifest.trackUri(t))
                } catch (e: Exception) {
                    Timber.w(e, "audio remove %s", t.id)
                }
            }
        }, "audio-remove").start()
    }

    internal fun markDownloaded(context: Context, manifest: AudioManifest) {
        setDownloaded(context, manifest.key, true)
        AudioRepository.saveOffline(context, manifest.key, manifest.raw)
    }
}

@OptIn(UnstableApi::class)
class AudioDownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Volatile
    private var writer: CacheWriter? = null
    private var lastPct = -1

    override fun doWork(): Result {
        val key = ChapterKey.parse(inputData.getString(KEY_CHAPTER)) ?: return Result.failure()
        val manifest = AudioRepository.load(applicationContext, key)
            ?: return if (runAttemptCount < 3) Result.retry() else Result.failure()
        val total = manifest.totalBytes.coerceAtLeast(1L)
        var done = 0L

        return try {
            manifest.tracks.forEach { track ->
                if (isStopped) return Result.failure()
                val dataSource = AudioCache.downloadFactory(applicationContext).createDataSource()
                val base = done
                val w = CacheWriter(dataSource, DataSpec(Uri.parse(manifest.trackUri(track))), null) { _, cached, _ ->
                    val pct = (((base + cached) * 100) / total).toInt().coerceIn(0, 99)
                    if (pct != lastPct) {
                        lastPct = pct
                        setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, pct).build())
                    }
                }
                writer = w
                w.cache()
                done += if (track.bytes > 0) track.bytes else 0
            }
            AudioDownloads.markDownloaded(applicationContext, manifest)
            Result.success()
        } catch (e: Exception) {
            Timber.w(e, "audio download %s", key.id)
            if (isStopped || runAttemptCount >= 3) Result.failure() else Result.retry()
        }
    }

    override fun onStopped() {
        super.onStopped()
        writer?.cancel()
    }

    companion object {
        const val KEY_CHAPTER = "chapter"
        const val KEY_PROGRESS = "progress"
    }
}
