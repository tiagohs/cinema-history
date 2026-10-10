package com.tiagohs.cinema_history.audio.ui

import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.audio.AudioAccess
import com.tiagohs.cinema_history.audio.AudioConfig
import com.tiagohs.cinema_history.audio.AudioDownloadWorker
import com.tiagohs.cinema_history.audio.AudioDownloads
import com.tiagohs.cinema_history.audio.AudioHost
import com.tiagohs.cinema_history.audio.AudioManifest
import com.tiagohs.cinema_history.audio.AudioSleepTimer
import com.tiagohs.cinema_history.audio.ChapterAudioController
import com.tiagohs.cinema_history.audio.ChapterKey
import com.tiagohs.cinema_history.databinding.FragmentAudioPlayerSheetBinding
import com.tiagohs.cinema_history.databinding.ItemAudioTrackBinding
import java.text.NumberFormat

/** Player expandido: faixas, velocidade, timer de sono, ouvir a era inteira e download offline. */
class AudioPlayerSheet : BottomSheetDialogFragment(), ChapterAudioController.Listener {

    private var _binding: FragmentAudioPlayerSheetBinding? = null
    private val binding get() = _binding!!

    private val audio: ChapterAudioController? get() = (activity as? AudioHost)?.audio

    private var seeking = false
    private var renderedTracksFor: String? = null
    private var downloadKey: ChapterKey? = null
    private var downloadLive: LiveData<List<WorkInfo>>? = null
    private var downloadInfo: WorkInfo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAudioPlayerSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val audio = audio ?: run {
            dismissAllowingStateLoss()
            return
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            // Tablets: o player não estica a tela toda; fica centralizado com largura máxima (M3: 640dp).
            val sheetMaxWidth = resources.getDimensionPixelSize(R.dimen.ls_sheet_max_width)
            if (sheetMaxWidth > 0) maxWidth = sheetMaxWidth
        }
        // edge-to-edge: conteúdo acima da barra de navegação
        ViewCompat.setOnApplyWindowInsetsListener(binding.audioSheetContent) { v, insets ->
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = nav.bottom + (16 * resources.displayMetrics.density).toInt())
            insets
        }

        binding.audioSheetPlayPause.setOnClickListener { audio.togglePlayPause() }
        binding.audioSheetBack15.setOnClickListener { audio.seekBack() }
        binding.audioSheetForward15.setOnClickListener { audio.seekForward() }
        binding.audioSheetPrevious.setOnClickListener { audio.previous() }
        binding.audioSheetNext.setOnClickListener { audio.next() }
        binding.audioSheetUnlock.setOnClickListener { audio.openPaywall() }
        binding.audioSheetSpeed.setOnClickListener { showSpeedMenu(it) }
        binding.audioSheetTimer.setOnClickListener { showTimerMenu(it) }
        binding.audioSheetEraMode.setOnClickListener {
            audio.setEraMode(binding.audioSheetEraMode.isChecked)
            binding.audioSheetEraMode.isChecked = audio.eraMode()
        }
        binding.audioSheetDownload.setOnClickListener { onDownloadClicked() }
        binding.audioSheetClose.setOnClickListener {
            audio.stop()
            dismiss()
        }

        binding.audioSheetSeek.setLabelFormatter { formatTime((it * audio.durationMs()).toLong()) }
        binding.audioSheetSeek.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                seeking = true
            }

            override fun onStopTrackingTouch(slider: Slider) {
                seeking = false
                audio.seekTo((slider.value * audio.durationMs()).toLong())
            }
        })
        binding.audioSheetSeek.addOnChangeListener { _, value, fromUser ->
            if (fromUser) binding.audioSheetElapsed.text = formatTime((value * audio.durationMs()).toLong())
        }

        audio.addListener(this)
        onAudioChanged()
    }

    override fun onDestroyView() {
        audio?.removeListener(this)
        downloadLive?.removeObservers(viewLifecycleOwner)
        _binding = null
        super.onDestroyView()
    }

    override fun onAudioChanged() {
        val b = _binding ?: return
        val audio = audio ?: return
        if (!audio.hasQueue()) {
            dismissAllowingStateLoss()
            return
        }
        val manifest = audio.currentManifest()
        val key = audio.currentKey()
        val locked = audio.access != AudioAccess.Level.UNLOCKED

        b.audioSheetTitle.text = audio.currentTitle()
        b.audioSheetChapter.text = if (key != null) {
            getString(R.string.audio_chapter_label, key.page, manifest?.title.orEmpty())
        } else ""
        b.audioSheetPreview.isVisible = locked

        // progresso
        val duration = audio.durationMs()
        val position = audio.positionMs().coerceAtMost(if (duration > 0) duration else Long.MAX_VALUE)
        if (!seeking) {
            b.audioSheetSeek.value = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
            b.audioSheetElapsed.text = formatTime(position)
        }
        b.audioSheetRemaining.text = if (duration > 0) "-" + formatTime(duration - position) else ""
        b.audioSheetSeek.isEnabled = duration > 0

        val playing = audio.isActive()
        b.audioSheetPlayPause.setImageResource(if (playing) R.drawable.ic_audio_pause else R.drawable.ic_audio_play)
        b.audioSheetPlayPause.contentDescription = getString(if (playing) R.string.audio_pause else R.string.audio_play)
        b.audioSheetNext.isEnabled = audio.hasNext() || locked
        b.audioSheetNext.alpha = if (b.audioSheetNext.isEnabled) 1f else 0.38f

        // velocidade e timer
        val speedText = formatSpeed(audio.speed())
        b.audioSheetSpeed.text = speedText
        b.audioSheetSpeed.contentDescription = getString(R.string.audio_speed_cd, speedText)
        b.audioSheetTimer.text = when {
            AudioSleepTimer.endOfTrack -> getString(R.string.audio_sleep_end_of_track)
            AudioSleepTimer.endsAt > 0 -> getString(
                R.string.audio_sleep_remaining, DateUtils.formatElapsedTime(AudioSleepTimer.remainingMs() / 1000)
            )
            else -> getString(R.string.audio_sleep_timer)
        }

        // era inteira
        b.audioSheetEraMode.isChecked = !locked && audio.eraMode()

        // faixas + download (dependem do capítulo atual)
        if (manifest != null) renderTracks(audio, manifest, locked)
        if (key != null && key != downloadKey) observeDownload(key)
        renderDownload(manifest, locked)
    }

    // --- faixas -------------------------------------------------------------------------------

    private fun renderTracks(audio: ChapterAudioController, m: AudioManifest, locked: Boolean) {
        val container = binding.audioSheetTracks
        val signature = "${m.key.id}|$locked"
        if (renderedTracksFor != signature) {
            renderedTracksFor = signature
            container.removeAllViews()
            m.tracks.forEachIndexed { i, t ->
                val row = ItemAudioTrackBinding.inflate(layoutInflater, container, false)
                row.audioTrackTitle.text = t.title
                row.audioTrackDuration.text = if (t.durationS > 0) formatTime(t.durationMs) else ""
                row.root.setOnClickListener { audio.playTrack(m, i) }
                container.addView(row.root)
            }
        }
        val current = audio.currentTrackIndex()
        for (i in 0 until container.childCount) {
            val row = ItemAudioTrackBinding.bind(container.getChildAt(i))
            val t = m.tracks.getOrNull(i) ?: continue
            val isLocked = locked && !t.isFree
            val isCurrent = i == current
            row.audioTrackNumber.text = (i + 1).toString()
            row.audioTrackNumber.isVisible = !isLocked && !isCurrent
            row.audioTrackIcon.isVisible = isLocked || isCurrent
            row.audioTrackIcon.setImageResource(if (isCurrent) R.drawable.ic_audio_equalizer else R.drawable.ic_audio_lock)
            row.audioTrackTitle.setTypeface(null, if (isCurrent) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
            row.root.isSelected = isCurrent
            row.root.contentDescription = when {
                isCurrent -> getString(R.string.audio_current_track_cd, t.title)
                isLocked -> getString(R.string.audio_locked_track_cd, t.title)
                else -> null
            }
        }
    }

    // --- velocidade / timer -------------------------------------------------------------------

    private fun formatSpeed(speed: Float): String {
        val nf = NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
        return nf.format(speed.toDouble()) + "×"
    }

    private fun showSpeedMenu(anchor: View) {
        val audio = audio ?: return
        PopupMenu(requireContext(), anchor).apply {
            AudioConfig.SPEEDS.forEachIndexed { i, s -> menu.add(Menu.NONE, i, i, formatSpeed(s)) }
            setOnMenuItemClickListener {
                audio.setSpeed(AudioConfig.SPEEDS[it.itemId])
                true
            }
            show()
        }
    }

    private fun showTimerMenu(anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            menu.add(Menu.NONE, 0, 0, getString(R.string.audio_sleep_off))
            AudioConfig.SLEEP_MINUTES.forEachIndexed { i, min ->
                menu.add(Menu.NONE, 100 + min, i + 1, getString(R.string.audio_sleep_minutes, min))
            }
            menu.add(Menu.NONE, 1, 99, getString(R.string.audio_sleep_end_of_track))
            setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> AudioSleepTimer.cancel()
                    1 -> AudioSleepTimer.startEndOfTrack()
                    else -> AudioSleepTimer.start(it.itemId - 100)
                }
                onAudioChanged()
                true
            }
            show()
        }
    }

    // --- download -----------------------------------------------------------------------------

    private fun observeDownload(key: ChapterKey) {
        downloadLive?.removeObservers(viewLifecycleOwner)
        downloadKey = key
        downloadInfo = null
        downloadLive = AudioDownloads.observe(requireContext(), key).also { live ->
            live.observe(viewLifecycleOwner, Observer { infos ->
                downloadInfo = infos?.lastOrNull()
                onAudioChanged()
            })
        }
    }

    private fun renderDownload(m: AudioManifest?, locked: Boolean) {
        val b = _binding ?: return
        val button = b.audioSheetDownload
        val progress = b.audioSheetDownloadProgress
        if (m == null) {
            button.isVisible = false
            progress.isVisible = false
            return
        }
        button.isVisible = true
        val ctx = requireContext()
        val info = downloadInfo
        val running = info != null && (info.state == WorkInfo.State.RUNNING || info.state == WorkInfo.State.ENQUEUED)
        when {
            AudioDownloads.isDownloaded(ctx, m.key) && !running -> {
                button.text = getString(R.string.audio_downloaded)
                button.contentDescription = getString(R.string.audio_downloaded_cd)
                button.setIconResource(R.drawable.ic_audio_download_done)
                button.isEnabled = true
                progress.isVisible = false
            }
            running -> {
                val pct = info!!.progress.getInt(AudioDownloadWorker.KEY_PROGRESS, 0)
                button.text = getString(R.string.audio_downloading, pct)
                button.contentDescription = null
                button.setIconResource(R.drawable.ic_audio_download)
                button.isEnabled = false
                progress.isVisible = true
                progress.progress = pct
            }
            info?.state == WorkInfo.State.FAILED -> {
                button.text = getString(R.string.audio_download_failed)
                button.contentDescription = null
                button.setIconResource(R.drawable.ic_audio_download)
                button.isEnabled = true
                progress.isVisible = false
            }
            else -> {
                val size = Formatter.formatShortFileSize(ctx, m.totalBytes)
                button.text = getString(R.string.audio_download, size)
                button.contentDescription = null
                button.setIconResource(if (locked) R.drawable.ic_audio_lock else R.drawable.ic_audio_download)
                button.isEnabled = true
                progress.isVisible = false
            }
        }
    }

    private fun onDownloadClicked() {
        val audio = audio ?: return
        val m = audio.currentManifest() ?: return
        val ctx = requireContext()
        if (audio.access != AudioAccess.Level.UNLOCKED) {
            audio.openPaywall()
            return
        }
        if (AudioDownloads.isDownloaded(ctx, m.key)) {
            AudioDownloads.remove(ctx, m)
            downloadInfo = null
        } else {
            AudioDownloads.start(ctx, m.key)
        }
        onAudioChanged()
    }

    private fun formatTime(ms: Long): String = DateUtils.formatElapsedTime((ms.coerceAtLeast(0L)) / 1000)

    companion object {
        const val TAG = "AudioPlayerSheet"
    }
}
