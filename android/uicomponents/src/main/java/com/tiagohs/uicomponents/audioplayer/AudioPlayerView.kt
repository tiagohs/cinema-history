package com.tiagohs.uicomponents.audioplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.domain.managers.AudioManager
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.*
import com.tiagohs.uicomponents.R
import com.tiagohs.uicomponents.databinding.ViewAudioPlayerBinding

class AudioPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    val audioManager: AudioManager
    private var isReady: Boolean = false

    private val binding = ViewAudioPlayerBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        this.audioManager = AudioManager()

        this.audioManager.onUpdateTimer = this.onUpdateTimer()
        this.audioManager.onError = this.onError()

        configureSeekBar()
        configureControll()

        isReady = false
    }

    var url: String = ""
    var image: Image? = null

    fun setAudioUrl(url: String) {
        this.url = url
    }

    fun setAudioImage(image: Image) {
        this.image = image
    }

    fun prepare(onReady: ((totalDuration: Int, displayTotalTime: String, displayElapsedTime: String,  mediaPlayer: MediaPlayer) -> Unit)? = null) {
        prepareResetLayout()
        startLoading()

        if (!isReady) {
            this.audioManager.prepareAudioPlayer(this.url, context) { totalDuration, displayTotalTime, displayElapsedTime, mediaPlayer ->
                isReady = true

                stopLoading()
                prepareReadyLayout(totalDuration, displayTotalTime, displayElapsedTime)

                onReady?.invoke(totalDuration, displayTotalTime, displayElapsedTime, mediaPlayer)
            }
        }

    }

    fun playOrPause() {
        this.audioManager.play(binding.progressSeekBar.progress) { isPlaying ->
            if (isPlaying) {
                binding.controlButton.setImageDrawable(context.getResourceDrawable(R.drawable.ic_pause_black_24dp))
            } else {
                binding.controlButton.setImageDrawable(context.getResourceDrawable(R.drawable.ic_play_arrow_grey_24dp))
            }
        }
    }

    fun onDestroy() {
        audioManager.onDestroy()
    }

    fun onUpdateTimer(): (currentPosition: Int, elapsedTime: String) -> Unit = { currentPosition, elapsedTime ->
        binding.progressSeekBar.progress = currentPosition;
        binding.progressTime.setResourceText(elapsedTime)
    }

    fun onError(): (message: String, error: Throwable) -> Unit = { message, error ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun configureControll() {
        binding.controlButton.setOnClickListener {
            playOrPause()
        }
    }

    private fun configureSeekBar() {
        binding.progressSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                audioManager.moveTo(seekBar.progress)
            }
        })
    }

    private fun prepareResetLayout() {
        binding.progressSeekBar.isEnabled = false
        binding.controlButton.isEnabled = false
    }

    private fun startLoading() {
        binding.audioImageContainer.hide()
        binding.audioLoadingProgress.show()
    }

    private fun stopLoading() {
        binding.audioImageContainer.show()
        binding.audioLoadingProgress.hide()
    }

    private fun prepareReadyLayout(totalDuration: Int, displayTotalTime: String, displayElapsedTime: String) {
        binding.progressSeekBar.isEnabled = true
        binding.controlButton.isEnabled = true

        binding.progressSeekBar.max = totalDuration
        binding.controlButton.setImageDrawable(context.getResourceDrawable(R.drawable.ic_play_arrow_grey_24dp))

        binding.progressTime.setResourceText(displayElapsedTime)
        binding.totalTime.setResourceText(displayTotalTime)

        image?.let { loadImage(it) }
    }

    private fun loadImage(image: Image) {
        binding.audioImage.loadImage(image)
    }

}