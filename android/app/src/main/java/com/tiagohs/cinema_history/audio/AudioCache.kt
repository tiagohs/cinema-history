package com.tiagohs.cinema_history.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * Cache em disco do Media3 usado SÓ para os capítulos baixados para ouvir offline.
 *
 * - Sem despejo automático (NoOpCacheEvictor): o que foi baixado fica até o usuário remover.
 * - O streaming normal NÃO grava no cache (cacheWriteDataSinkFactory = null), então o espaço só
 *   cresce com downloads explícitos. Na reprodução, o que está no cache é lido do disco; o resto vem da rede.
 */
@OptIn(UnstableApi::class)
object AudioCache {

    @Volatile
    private var cache: SimpleCache? = null

    fun get(context: Context): SimpleCache = cache ?: synchronized(this) {
        cache ?: SimpleCache(
            File(context.applicationContext.filesDir, "audio/cache"),
            NoOpCacheEvictor(),
            StandaloneDatabaseProvider(context.applicationContext)
        ).also { cache = it }
    }

    /** http(s) e asset:/// (fonte de teste). */
    private fun upstream(context: Context): DataSource.Factory =
        DefaultDataSource.Factory(
            context.applicationContext,
            DefaultHttpDataSource.Factory()
                .setUserAgent("CinemaHistory-Android")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(10_000)
                .setReadTimeoutMs(15_000)
        )

    /** Para o player: lê do cache se baixado, senão da rede, sem gravar. */
    fun playbackFactory(context: Context): DataSource.Factory =
        CacheDataSource.Factory()
            .setCache(get(context))
            .setUpstreamDataSourceFactory(upstream(context))
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    /** Para o download: lê da rede e grava no cache. */
    fun downloadFactory(context: Context): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(get(context))
            .setUpstreamDataSourceFactory(upstream(context))
}
