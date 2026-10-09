package com.tiagohs.cinema_history.audio.ui

import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.audio.AudioAccess
import com.tiagohs.cinema_history.audio.ChapterAudioController
import com.tiagohs.cinema_history.databinding.ViewAudioMiniPlayerBinding

/**
 * Mini-player (barra fina acima do rodapé). Visível quando há fila no player, o rodapé está à mostra
 * e o anúncio nativo da página não está perto dele.
 */
class AudioMiniPlayer(
    private val binding: ViewAudioMiniPlayerBinding,
    private val audio: ChapterAudioController,
    private val footerHeight: () -> Int,
    private val onExpand: () -> Unit
) : ChapterAudioController.Listener {

    private val root: View = binding.root
    private val context = root.context

    private var footerShown = true
    private var adConflict = false
    private var shown = false
    private var lastPlaying: Boolean? = null

    init {
        binding.audioMiniPlayPause.setOnClickListener { audio.togglePlayPause() }
        binding.audioMiniBack15.setOnClickListener { audio.seekBack() }
        binding.audioMiniExpand.setOnClickListener { onExpand() }
        binding.audioMiniPlayerCard.setOnClickListener { onExpand() }
        audio.addListener(this)
        onAudioChanged()
    }

    fun setFooterShown(visible: Boolean) {
        footerShown = visible
        updateVisibility()
    }

    fun setAdConflict(conflict: Boolean) {
        if (adConflict == conflict) return
        adConflict = conflict
        updateVisibility()
    }

    /** Posição de tela do mini-player quando visível (sem a translação de esconder). */
    fun slotOnScreen(): Rect? {
        if (!audio.hasQueue() || root.visibility == View.GONE || root.height == 0) return null
        val loc = IntArray(2)
        root.getLocationOnScreen(loc)
        val top = loc[1] - root.translationY.toInt()
        return Rect(loc[0], top, loc[0] + root.width, top + root.height)
    }

    fun reservedHeight(): Int {
        if (!audio.hasQueue()) return 0
        val h = if (root.height > 0) root.height else (48 * context.resources.displayMetrics.density).toInt()
        return h + (12 * context.resources.displayMetrics.density).toInt()
    }

    override fun onAudioChanged() {
        val hasQueue = audio.hasQueue() && audio.access != AudioAccess.Level.HIDDEN
        if (!hasQueue) {
            root.animate().cancel()
            root.visibility = View.GONE
            shown = false
            return
        }
        val playing = audio.isActive()
        if (lastPlaying != playing) {
            lastPlaying = playing
            binding.audioMiniPlayPause.setImageResource(if (playing) R.drawable.ic_audio_pause else R.drawable.ic_audio_play)
            binding.audioMiniPlayPause.contentDescription =
                context.getString(if (playing) R.string.audio_pause else R.string.audio_play)
        }
        binding.audioMiniBuffering.isVisible = audio.isBuffering()

        val title = audio.currentTitle()?.toString().orEmpty()
        if (binding.audioMiniTitle.text?.toString() != title) {
            binding.audioMiniTitle.text = title
            binding.audioMiniPlayerCard.contentDescription = context.getString(R.string.audio_mini_player_cd, title)
        }
        val duration = audio.durationMs()
        binding.audioMiniProgress.progress =
            if (duration > 0) ((audio.positionMs() * 1000) / duration).toInt().coerceIn(0, 1000) else 0

        updateVisibility()
    }

    private fun updateVisibility() {
        if (!audio.hasQueue() || audio.access == AudioAccess.Level.HIDDEN) return
        val want = footerShown && !adConflict
        if (root.visibility == View.GONE) {
            // primeira vez: entra já na posição certa
            root.visibility = if (want) View.VISIBLE else View.INVISIBLE
            root.translationY = if (want) 0f else hiddenOffset()
            root.alpha = if (want) 1f else 0f
            shown = want
            return
        }
        if (want == shown) return
        shown = want
        root.animate().cancel()
        if (want) {
            root.visibility = View.VISIBLE
            root.animate().translationY(0f).alpha(1f)
                .setInterpolator(DecelerateInterpolator(2f))
                .withEndAction(null)
                .start()
        } else {
            root.animate().translationY(hiddenOffset()).alpha(0f)
                .setInterpolator(AccelerateInterpolator(2f))
                .withEndAction { if (!shown && root.visibility == View.VISIBLE) root.visibility = View.INVISIBLE }
                .start()
        }
    }

    private fun hiddenOffset(): Float {
        val h = if (root.height > 0) root.height else (48 * context.resources.displayMetrics.density).toInt()
        return (h + footerHeight() + (12 * context.resources.displayMetrics.density)).toFloat()
    }

    fun release() {
        audio.removeListener(this)
    }
}
