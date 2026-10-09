package com.tiagohs.cinema_history.audio

import android.content.ComponentName
import android.graphics.Rect
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.support.Supporter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

/** Activity que hospeda o player (HistoryPagesActivity). */
interface AudioHost {
    val audio: ChapterAudioController
    fun openAudioSheet()

    /** Onde o mini-player fica quando visível (coordenadas de tela), ou null se não há fila. */
    fun miniPlayerSlot(): Rect?

    /** Espaço extra no fim da lista para o mini-player não cobrir o último parágrafo. */
    fun miniPlayerReservedHeight(): Int

    /** A página avisa quando o anúncio nativo está perto do mini-player (aí ele some). */
    fun setMiniPlayerAdConflict(conflict: Boolean)
}

/**
 * Ponte entre a tela do capítulo e o AudioPlaybackService (MediaController do Media3).
 * Um por Activity. Monta a fila (playlist) com as faixas do capítulo, aplica o acesso
 * (apoiador / prévia grátis da "Abertura" / escondido) e avisa os ouvintes (página, mini-player, sheet).
 */
@OptIn(UnstableApi::class)
class ChapterAudioController(
    private val activity: FragmentActivity,
    /** Ids das páginas (capítulos) da era aberta, na ordem, para "Ouvir a era inteira". */
    private val eraPages: () -> Pair<Int, List<Int>>?
) : DefaultLifecycleObserver {

    fun interface Listener {
        fun onAudioChanged()
    }

    private val app = activity.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private val listeners = LinkedHashSet<Listener>()
    private val pending = ArrayList<(MediaController) -> Unit>()
    private val manifests = HashMap<String, AudioManifest>()
    private val loading = HashSet<String>()
    private val disposables = CompositeDisposable()
    private var eraDisposable: Disposable? = null

    private var future: ListenableFuture<MediaController>? = null
    var controller: MediaController? = null
        private set

    var access: AudioAccess.Level = AudioAccess.get(app)
        private set

    private var started = false

    private val supporterListener: () -> Unit = { handler.post { refreshAccess() } }
    private val sleepListener: () -> Unit = { notifyChanged() }

    private val ticker = object : Runnable {
        override fun run() {
            notifyChanged()
            if (started && controller?.isPlaying == true) handler.postDelayed(this, TICK_MS)
        }
    }

    init {
        activity.lifecycle.addObserver(this)
        Supporter.addListener(supporterListener)
        AudioSleepTimer.addListener(sleepListener)
    }

    // --- ciclo de vida ------------------------------------------------------------------------

    override fun onStart(owner: LifecycleOwner) {
        started = true
        refreshAccess()
        if (AudioPlaybackService.isAlive) connect()
        restartTicker()
    }

    override fun onStop(owner: LifecycleOwner) {
        started = false
        handler.removeCallbacks(ticker)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Supporter.removeListener(supporterListener)
        AudioSleepTimer.removeListener(sleepListener)
        handler.removeCallbacksAndMessages(null)
        disposables.dispose()
        eraDisposable?.dispose()
        controller?.removeListener(playerListener)
        future?.let { MediaController.releaseFuture(it) }
        future = null
        controller = null
        listeners.clear()
    }

    fun addListener(l: Listener) {
        listeners += l
    }

    fun removeListener(l: Listener) {
        listeners -= l
    }

    private fun notifyChanged() = listeners.toList().forEach { it.onAudioChanged() }

    private fun restartTicker() {
        handler.removeCallbacks(ticker)
        if (started && controller?.isPlaying == true) handler.postDelayed(ticker, TICK_MS)
    }

    // --- conexão com o serviço ----------------------------------------------------------------

    private fun withController(action: (MediaController) -> Unit) {
        controller?.let {
            action(it)
            return
        }
        pending += action
        connect()
    }

    private fun connect() {
        if (future != null) return
        val token = SessionToken(app, ComponentName(app, AudioPlaybackService::class.java))
        val f = MediaController.Builder(app, token).buildAsync()
        future = f
        f.addListener({
            val c = try {
                f.get()
            } catch (e: Exception) {
                Timber.w(e, "audio controller")
                future = null
                pending.clear()
                return@addListener
            }
            if (activity.isDestroyed) {
                MediaController.releaseFuture(f)
                return@addListener
            }
            controller = c
            c.addListener(playerListener)
            pending.toList().forEach { it(c) }
            pending.clear()
            ensureQueueManifests()
            restartTicker()
            notifyChanged()
        }, ContextCompat.getMainExecutor(app))
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) restartTicker()
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) || events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                ensureQueueManifests()
            }
            if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) && player.playbackState == Player.STATE_ENDED) {
                // prévia grátis terminou → convite para apoiar
                if (access == AudioAccess.Level.LOCKED) openPaywall()
            }
            notifyChanged()
        }

        override fun onPlayerError(error: PlaybackException) {
            Timber.w(error, "audio playback")
            if (started) Toast.makeText(app, R.string.audio_error_playback, Toast.LENGTH_SHORT).show()
        }
    }

    // --- estado -------------------------------------------------------------------------------

    fun hasQueue(): Boolean = (controller?.mediaItemCount ?: 0) > 0

    fun isPlaying(): Boolean = controller?.isPlaying == true

    /** Tocando ou carregando para tocar. */
    fun isActive(): Boolean = controller?.let { it.playWhenReady && it.playbackState != Player.STATE_ENDED } == true

    fun isBuffering(): Boolean = controller?.playbackState == Player.STATE_BUFFERING

    fun currentKey(): ChapterKey? = AudioIds.chapterOf(controller?.currentMediaItem?.mediaId)

    fun currentTrackId(): String? = AudioIds.trackOf(controller?.currentMediaItem?.mediaId)

    fun manifestFor(key: ChapterKey): AudioManifest? =
        manifests[key.id] ?: AudioRepository.cached(app, key)?.also { manifests[key.id] = it }

    fun currentManifest(): AudioManifest? = currentKey()?.let { manifestFor(it) }

    fun currentTrackIndex(): Int = currentManifest()?.indexOf(currentTrackId()) ?: -1

    fun currentTrack(): AudioTrack? = currentManifest()?.tracks?.getOrNull(currentTrackIndex())

    fun currentTitle(): CharSequence? = controller?.mediaMetadata?.title ?: currentTrack()?.title

    /** "Capítulo 1 · Visionários" da faixa que está tocando (no modo era, muda junto com o capítulo). */
    fun currentChapterLabel(): String? {
        val key = currentKey() ?: return null
        val title = currentManifest()?.title?.takeIf { it.isNotBlank() }
            // sem o manifest em memória: o "artista" do item já é o rótulo pronto (ver toMediaItem)
            ?: return controller?.mediaMetadata?.artist?.toString()?.takeIf { it.isNotBlank() }
        return chapterLabel(key, title)
    }

    fun positionMs(): Long = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L

    fun durationMs(): Long {
        val d = controller?.duration ?: C.TIME_UNSET
        if (d != C.TIME_UNSET && d > 0) return d
        return currentTrack()?.durationMs ?: 0L
    }

    /** Item do content_list sendo lido agora (para destacar o parágrafo), ou null. */
    fun currentSourceIndex(): Int? {
        val m = currentManifest() ?: return null
        return m.sourceIndexAt(currentTrackIndex(), positionMs())
    }

    fun speed(): Float = controller?.playbackParameters?.speed ?: AudioPositions.speed(app)

    fun eraMode(): Boolean = AudioPositions.eraMode(app)

    fun hasNext(): Boolean = controller?.hasNextMediaItem() == true

    fun hasPrevious(): Boolean = controller?.hasPreviousMediaItem() == true

    fun registerManifest(manifest: AudioManifest) {
        manifests[manifest.key.id] = manifest
    }

    /** A fila do player traz todas as faixas deste capítulo (e não só a prévia)? */
    private fun queueHasFullChapter(c: MediaController, m: AudioManifest): Boolean =
        (0 until c.mediaItemCount).count { AudioIds.chapterOf(c.getMediaItemAt(it).mediaId) == m.key } == m.tracks.size

    /** Manifests das faixas na fila (por ex. depois de reabrir a tela com o áudio tocando). */
    private fun ensureQueueManifests() {
        val c = controller ?: return
        val keys = (0 until c.mediaItemCount).mapNotNull { AudioIds.chapterOf(c.getMediaItemAt(it).mediaId) }.toSet()
        keys.filter { manifestFor(it) == null && loading.add(it.id) }.forEach { key ->
            disposables.add(AudioRepository.manifest(app, key).subscribe(
                { m -> loading -= key.id; registerManifest(m); notifyChanged() },
                { loading -= key.id },
                { loading -= key.id }
            ))
        }
    }

    // --- ações --------------------------------------------------------------------------------

    /**
     * Toca o capítulo. Sem [trackIndex], retoma de onde parou (AudioPositions).
     * Na prévia (LOCKED) só a faixa "00" entra na fila; pedir outra faixa abre a tela de apoio.
     */
    fun play(manifest: AudioManifest, trackIndex: Int? = null, positionMs: Long? = null) {
        registerManifest(manifest)
        access = AudioAccess.get(app)
        if (access == AudioAccess.Level.HIDDEN) return

        val resume = if (trackIndex == null) AudioPositions.get(app, manifest.key) else null
        var index = trackIndex ?: manifest.indexOf(resume?.first).takeIf { it >= 0 } ?: 0
        var pos = positionMs ?: resume?.second ?: 0L
        index = index.coerceIn(0, manifest.tracks.size - 1)

        if (access == AudioAccess.Level.LOCKED && !manifest.tracks[index].isFree) {
            if (trackIndex != null) {
                openPaywall()
                return
            }
            index = manifest.tracks.indexOfFirst { it.isFree }
            if (index < 0) {
                openPaywall()
                return
            }
            pos = 0L
        }
        val track = manifest.tracks[index]
        if (track.durationMs > 0 && pos > track.durationMs - 2_000) pos = 0L

        withController { c ->
            val targetId = AudioIds.mediaId(manifest.key, track.id)
            val existing = (0 until c.mediaItemCount).firstOrNull { c.getMediaItemAt(it).mediaId == targetId }
            val reuse = existing != null &&
                    (access == AudioAccess.Level.LOCKED || queueHasFullChapter(c, manifest))
            if (reuse) {
                c.seekTo(existing!!, pos)
                if (c.playbackState == Player.STATE_IDLE) c.prepare()
            } else {
                val tracks = if (access == AudioAccess.Level.UNLOCKED) manifest.tracks else listOf(track)
                c.setMediaItems(tracks.map { toMediaItem(manifest, it) }, tracks.indexOf(track), pos)
                c.prepare()
                if (access == AudioAccess.Level.UNLOCKED && eraMode()) appendEra(manifest.key)
            }
            c.setPlaybackSpeed(AudioPositions.speed(app))
            c.play()
            notifyChanged()
        }
    }

    /** "Ouvir a partir daqui": item do content_list → segmento → faixa + posição. */
    fun playFromSource(manifest: AudioManifest, sourceIndex: Int) {
        val (track, pos) = manifest.locate(sourceIndex) ?: return
        play(manifest, track, pos)
    }

    fun playTrack(manifest: AudioManifest, index: Int) = play(manifest, index, 0L)

    fun togglePlayPause() {
        val c = controller ?: return
        when {
            c.isPlaying -> c.pause()
            c.playbackState == Player.STATE_ENDED -> {
                if (access == AudioAccess.Level.LOCKED) {
                    openPaywall()
                } else {
                    c.seekTo(0, 0L)
                    c.play()
                }
            }
            else -> {
                if (c.playbackState == Player.STATE_IDLE) c.prepare()
                c.play()
            }
        }
    }

    fun seekBack() = controller?.seekBack() ?: Unit

    fun seekForward() = controller?.seekForward() ?: Unit

    fun seekTo(positionMs: Long) = controller?.seekTo(positionMs) ?: Unit

    fun next() {
        if (access != AudioAccess.Level.UNLOCKED) {
            openPaywall()
            return
        }
        controller?.takeIf { it.hasNextMediaItem() }?.seekToNextMediaItem()
    }

    fun previous() {
        val c = controller ?: return
        if (c.hasPreviousMediaItem() && c.currentPosition < 3_000) c.seekToPreviousMediaItem() else c.seekTo(0L)
    }

    fun setSpeed(speed: Float) {
        AudioPositions.setSpeed(app, speed)
        controller?.setPlaybackSpeed(speed)
        notifyChanged()
    }

    /** Para a reprodução e esvazia a fila (o mini-player some). */
    fun stop() {
        eraDisposable?.dispose()
        AudioSleepTimer.cancel()
        controller?.let {
            it.pause()
            it.stop()
            it.clearMediaItems()
        }
        notifyChanged()
    }

    fun setEraMode(on: Boolean) {
        if (on && access != AudioAccess.Level.UNLOCKED) {
            openPaywall()
            notifyChanged()
            return
        }
        AudioPositions.setEraMode(app, on)
        val key = currentKey()
        if (key != null) {
            if (on) appendEra(key) else trimToChapter(key)
        }
        notifyChanged()
    }

    /** Acrescenta à fila os capítulos seguintes da era que têm áudio. */
    private fun appendEra(from: ChapterKey) {
        eraDisposable?.dispose()
        val (era, pages) = eraPages() ?: return
        if (era != from.era) return
        val start = pages.indexOf(from.page)
        if (start < 0) return
        eraDisposable = Observable.fromIterable(pages.drop(start + 1))
            .concatMapMaybe { page -> AudioRepository.manifest(app, ChapterKey(from.lang, from.era, page)) }
            .subscribe({ m ->
                registerManifest(m)
                val c = controller ?: return@subscribe
                if (!eraMode() || access != AudioAccess.Level.UNLOCKED) return@subscribe
                val already = (0 until c.mediaItemCount).any { AudioIds.chapterOf(c.getMediaItemAt(it).mediaId) == m.key }
                if (!already) c.addMediaItems(m.tracks.map { toMediaItem(m, it) })
                notifyChanged()
            }, { Timber.w(it, "audio era") })
    }

    private fun trimToChapter(key: ChapterKey) {
        eraDisposable?.dispose()
        val c = controller ?: return
        for (i in c.mediaItemCount - 1 downTo 0) {
            if (AudioIds.chapterOf(c.getMediaItemAt(i).mediaId) != key) c.removeMediaItem(i)
        }
    }

    // --- acesso (apoio) -----------------------------------------------------------------------

    fun openPaywall() {
        if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        Supporter.openSupportScreen(activity, PAYWALL_SOURCE)
    }

    /** Relê o acesso; se o usuário acabou de apoiar, libera a fila inteira na hora. */
    fun refreshAccess() {
        val old = access
        access = AudioAccess.get(app)
        if (old != AudioAccess.Level.UNLOCKED && access == AudioAccess.Level.UNLOCKED) upgradeQueue()
        if (old != access) notifyChanged()
    }

    private fun upgradeQueue() {
        val c = controller ?: return
        val m = currentManifest() ?: return
        if (queueHasFullChapter(c, m)) return
        var idx = m.indexOf(currentTrackId()).coerceAtLeast(0)
        var pos = c.currentPosition
        var play = c.playWhenReady
        if (c.playbackState == Player.STATE_ENDED && idx + 1 < m.tracks.size) {
            // a prévia tinha terminado: segue para a primeira faixa liberada
            idx += 1
            pos = 0L
            play = true
        }
        c.setMediaItems(m.tracks.map { toMediaItem(m, it) }, idx, pos)
        c.prepare()
        if (play) c.play()
        if (eraMode()) appendEra(m.key)
    }

    /** Rótulo no idioma do áudio (não depende do Locale do applicationContext, que no Android < 13 ignora o idioma do app). */
    private fun chapterLabel(key: ChapterKey, title: String): String {
        val res = labelContexts.getOrPut(key.lang) {
            val conf = android.content.res.Configuration(app.resources.configuration)
            conf.setLocale(java.util.Locale.forLanguageTag(if (key.lang == "pt") "pt-BR" else key.lang))
            app.createConfigurationContext(conf)
        }
        return res.getString(R.string.audio_chapter_label, key.page, title)
    }

    private val labelContexts = HashMap<String, android.content.Context>()

    private fun toMediaItem(m: AudioManifest, t: AudioTrack): MediaItem {
        val uri = m.trackUri(t)
        return MediaItem.Builder()
            .setMediaId(AudioIds.mediaId(m.key, t.id))
            .setUri(uri)
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(Uri.parse(uri)).build())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(t.title)
                    .setDisplayTitle(t.title)
                    // notificação/tela de bloqueio: "Abertura" + "Capítulo 1 · Visionários"
                    .setArtist(chapterLabel(m.key, m.title))
                    .setAlbumTitle(m.eraTitle ?: m.title)
                    // capa no player do sistema (notificação, tela de bloqueio, Bluetooth do carro)
                    .setArtworkUri(Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/drawable/img_color_movies"))
                    .setMediaType(MediaMetadata.MEDIA_TYPE_AUDIO_BOOK_CHAPTER)
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build()
            )
            .build()
    }

    companion object {
        const val PAYWALL_SOURCE = "audio"
        private const val TICK_MS = 500L
    }
}
