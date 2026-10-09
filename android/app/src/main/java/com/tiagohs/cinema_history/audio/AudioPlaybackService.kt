package com.tiagohs.cinema_history.audio

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Serviço de reprodução da narração (Media3): toca com a tela bloqueada, mostra a notificação de mídia
 * e responde a fones/Bluetooth (MediaSession). Pausa sozinho quando o fone é desconectado
 * (handleAudioBecomingNoisy) e respeita o foco de áudio (ligações, outros apps).
 *
 * Também salva a posição de cada capítulo (AudioPositions) para retomar de onde parou.
 */
@OptIn(UnstableApi::class)
class AudioPlaybackService : MediaSessionService() {

    private var session: MediaSession? = null
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastChapter: ChapterKey? = null

    private val saveTicker = object : Runnable {
        override fun run() {
            savePosition()
            handler.postDelayed(this, 5_000L)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val exo = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(AudioCache.playbackFactory(this)))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setSeekBackIncrementMs(AudioConfig.SEEK_INCREMENT_MS)
            .setSeekForwardIncrementMs(AudioConfig.SEEK_INCREMENT_MS)
            .build()
        exo.addListener(playerListener)
        player = exo
        AudioSleepTimer.attach(exo)

        val builder = MediaSession.Builder(this, exo).setCallback(sessionCallback)
        sessionActivity()?.let { builder.setSessionActivity(it) }
        // Notificação / tela bloqueada: voltar e avançar 15 s ao lado do play/pausa.
        builder.setMediaButtonPreferences(
            listOf(
                CommandButton.Builder(CommandButton.ICON_SKIP_BACK_15)
                    .setPlayerCommand(Player.COMMAND_SEEK_BACK)
                    .setDisplayName(getString(com.tiagohs.cinema_history.R.string.audio_back_15))
                    .setSlots(CommandButton.SLOT_BACK)
                    .build(),
                CommandButton.Builder(CommandButton.ICON_SKIP_FORWARD_15)
                    .setPlayerCommand(Player.COMMAND_SEEK_FORWARD)
                    .setDisplayName(getString(com.tiagohs.cinema_history.R.string.audio_forward_15))
                    .setSlots(CommandButton.SLOT_FORWARD)
                    .build()
            )
        )
        session = builder.build()
        isAlive = true
    }

    /** Toque na notificação: volta para o app (a tarefa existente, se houver). */
    private fun sessionActivity(): PendingIntent? {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return null
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        return PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        val p = player
        if (p == null || !p.playWhenReady || p.mediaItemCount == 0 || p.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(saveTicker)
        savePosition()
        player?.let {
            AudioSleepTimer.detach(it)
            it.removeListener(playerListener)
            it.release()
        }
        session?.release()
        session = null
        player = null
        isAlive = false
        super.onDestroy()
    }

    private fun savePosition() {
        val p = player ?: return
        val item = p.currentMediaItem ?: return
        val key = AudioIds.chapterOf(item.mediaId) ?: return
        val track = AudioIds.trackOf(item.mediaId) ?: return
        if (p.playbackState == Player.STATE_ENDED) return
        AudioPositions.save(this, key, track, p.currentPosition)
    }

    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            handler.removeCallbacks(saveTicker)
            if (isPlaying) handler.postDelayed(saveTicker, 5_000L) else savePosition()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val key = AudioIds.chapterOf(mediaItem?.mediaId)
            val previous = lastChapter
            // "Ouvir a era inteira": terminou um capítulo e começou o próximo → o anterior foi ouvido até o fim
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && previous != null && key != null && previous != key) {
                AudioPositions.clear(this@AudioPlaybackService, previous)
            }
            lastChapter = key
            savePosition()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                // ouviu até o fim: na próxima vez começa do início
                AudioIds.chapterOf(player?.currentMediaItem?.mediaId)?.let {
                    AudioPositions.clear(this@AudioPlaybackService, it)
                }
                AudioSleepTimer.cancel()
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (!playWhenReady && reason == Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) {
                AudioSleepTimer.onPausedAtEndOfTrack()
            }
        }
    }

    private val sessionCallback = object : MediaSession.Callback {
        /** O controller manda a URI em requestMetadata.mediaUri (padrão do Media3); aqui vira a URI do item. */
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val resolved = mediaItems.map { item ->
                val uri = item.requestMetadata.mediaUri ?: item.localConfiguration?.uri
                if (uri == null) item else item.buildUpon().setUri(uri).build()
            }.toMutableList()
            return Futures.immediateFuture(resolved)
        }
    }

    companion object {
        /** true enquanto o serviço existe neste processo (a tela conecta o controller logo ao abrir). */
        @Volatile
        var isAlive: Boolean = false
            private set
    }
}
