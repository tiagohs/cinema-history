package com.tiagohs.cinema_history.ui.custom

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.managers.AudioManager
import com.tiagohs.cinema_history.models.image.Image
import kotlinx.android.synthetic.main.view_audio_player.view.*

class AudioPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    private val audioManager: AudioManager
    private var isReady: Boolean = false

    init {
        View.inflate(context, R.layout.view_audio_player, this)

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
        this.audioManager.play(progressSeekBar.progress) { isPlaying ->
            if (isPlaying) {
                controlButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pause_black_24dp))
            } else {
                controlButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_play_arrow_grey_24dp))
            }
        }
    }

    fun onDestroy() {
        audioManager.onDestroy()
    }

    fun onUpdateTimer(): (currentPosition: Int, elapsedTime: String) -> Unit = { currentPosition, elapsedTime ->
        progressSeekBar.progress = currentPosition;
        progressTime.text = elapsedTime;
    }

    fun onError(): (message: String, error: Throwable) -> Unit = { message, error ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun configureControll() {
        controlButton.setOnClickListener {
            playOrPause()
        }
    }

    private fun configureSeekBar() {
        progressSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                audioManager.moveTo(seekBar.progress)
            }
        })
    }

    private fun prepareResetLayout() {
        progressSeekBar.isEnabled = false
        controlButton.isEnabled = false
    }

    private fun startLoading() {
        audioImageContainer.visibility = View.GONE
        audioLoadingProgress.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        audioImageContainer.visibility = View.VISIBLE
        audioLoadingProgress.visibility = View.GONE
    }

    private fun prepareReadyLayout(totalDuration: Int, displayTotalTime: String, displayElapsedTime: String) {
        progressSeekBar.isEnabled = true
        controlButton.isEnabled = true

        progressSeekBar.max = totalDuration
        controlButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_play_arrow_grey_24dp))

        progressTime.text = displayElapsedTime
        totalTime.text = displayTotalTime

        image?.let { loadImage(it) }
    }

    private fun loadImage(image: Image) {
        audioImage.loadImage(image)
    }

}