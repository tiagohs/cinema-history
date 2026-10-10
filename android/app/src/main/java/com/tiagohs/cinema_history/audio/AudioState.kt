package com.tiagohs.cinema_history.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.media3.exoplayer.ExoPlayer
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.support.Supporter

/** Posição salva por capítulo (para retomar de onde parou), velocidade e "ouvir a era inteira". */
object AudioPositions {

    private const val PREFS = "audio_positions"
    private const val KEY_SPEED = "_speed"
    private const val KEY_ERA_MODE = "_era_mode"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(context: Context, key: ChapterKey, trackId: String, positionMs: Long) {
        prefs(context).edit().putString(key.id, "$trackId|${positionMs.coerceAtLeast(0)}").apply()
    }

    /** (id da faixa, posição em ms) ou null. */
    fun get(context: Context, key: ChapterKey): Pair<String, Long>? {
        val v = prefs(context).getString(key.id, null) ?: return null
        val track = v.substringBefore('|')
        val pos = v.substringAfter('|', "0").toLongOrNull() ?: 0L
        return track to pos
    }

    fun clear(context: Context, key: ChapterKey) {
        prefs(context).edit().remove(key.id).apply()
    }

    fun speed(context: Context): Float = prefs(context).getFloat(KEY_SPEED, 1f)

    fun setSpeed(context: Context, speed: Float) {
        prefs(context).edit().putFloat(KEY_SPEED, speed).apply()
    }

    fun eraMode(context: Context): Boolean = prefs(context).getBoolean(KEY_ERA_MODE, false)

    fun setEraMode(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ERA_MODE, on).apply()
    }
}

/**
 * Timer de sono. Vive no processo do app (o MediaSessionService mantém o processo vivo enquanto toca)
 * e pausa o player do serviço diretamente.
 */
object AudioSleepTimer {

    private val handler = Handler(Looper.getMainLooper())
    private var player: ExoPlayer? = null
    private val listeners = LinkedHashSet<() -> Unit>()

    /** Momento (elapsedRealtime) em que o timer para, ou 0. */
    var endsAt: Long = 0L
        private set

    /** Pausa ao fim da faixa atual. */
    var endOfTrack: Boolean = false
        private set

    val isActive: Boolean get() = endsAt > 0L || endOfTrack

    fun remainingMs(): Long = if (endsAt > 0L) (endsAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L) else 0L

    private val fire = Runnable {
        player?.pause()
        cancel()
    }

    fun start(minutes: Int) {
        cancel()
        endsAt = SystemClock.elapsedRealtime() + minutes * 60_000L
        handler.postDelayed(fire, minutes * 60_000L)
        notifyChanged()
    }

    fun startEndOfTrack() {
        cancel()
        endOfTrack = true
        player?.pauseAtEndOfMediaItems = true
        notifyChanged()
    }

    fun cancel() {
        handler.removeCallbacks(fire)
        val wasActive = isActive
        if (endOfTrack) player?.pauseAtEndOfMediaItems = false
        endsAt = 0L
        endOfTrack = false
        if (wasActive) notifyChanged()
    }

    /** Chamado pelo serviço quando o player pausou no fim da faixa. */
    internal fun onPausedAtEndOfTrack() {
        if (endOfTrack) cancel()
    }

    internal fun attach(p: ExoPlayer) {
        player = p
    }

    internal fun detach(p: ExoPlayer) {
        if (player === p) {
            cancel()
            player = null
        }
    }

    fun addListener(l: () -> Unit) { listeners += l }
    fun removeListener(l: () -> Unit) { listeners -= l }
    private fun notifyChanged() = listeners.toList().forEach { it() }
}

/** Quem pode ouvir o quê (contrato em support/Supporter.kt). */
object AudioAccess {

    enum class Level {
        /** Sem oferta e não apoia: nenhum controle de áudio. */
        HIDDEN,
        /** Oferta disponível e não apoia: só a "Abertura" (faixa 00) toca; o resto abre a tela de apoio. */
        LOCKED,
        /** Apoiador: tudo liberado. */
        UNLOCKED
    }

    fun get(context: Context): Level {
        if (BuildConfig.DEBUG) {
            when (AudioConfig.accessOverride(context)) {
                "hidden" -> return Level.HIDDEN
                "locked" -> return Level.LOCKED
                "unlocked" -> return Level.UNLOCKED
            }
        }
        return when {
            Supporter.isSupporter(context) -> Level.UNLOCKED
            Supporter.isOfferAvailable(context) -> Level.LOCKED
            else -> Level.HIDDEN
        }
    }
}
