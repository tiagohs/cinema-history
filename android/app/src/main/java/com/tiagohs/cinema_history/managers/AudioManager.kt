package com.tiagohs.cinema_history.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AudioManager {

    private var context: Context? = null
    private var mediaPlayer: MediaPlayer? = null

    private var totalDuration: Int = 0
    private var currentPosition: Int = 0

    private var maxTime = ""
    private var progressTime = ""

    private var compositeDisposable = CompositeDisposable()

    var onUpdateTimer: ((currentPosition: Int, elapsedTime: String) -> Unit)? = null
    var onError: ((message: String, error: Throwable) -> Unit)? = null

    fun prepareAudioPlayer(audioUrl: String, context: Context, onReady: ((totalDuration: Int, displayTotalTime: String, displayElapsedTime: String,  mediaPlayer: MediaPlayer) -> Unit)? = null) {
        if (!compositeDisposable.isDisposed) { compositeDisposable = CompositeDisposable() }

        this.context = context

        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        this.mediaPlayer = MediaPlayer()

        configureMediaPlayer(audioUrl, onReady)
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    fun play(position: Int, onFinished: ((isPlaying: Boolean) -> Unit)? = null) {
        val mediaPlayer = this.mediaPlayer ?: return

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()

            onFinished?.invoke(false)

            compositeDisposable.dispose()
        } else {
            mediaPlayer.seekTo(position)
            mediaPlayer.start()

            configureReproducePosition()

            onFinished?.invoke(true)
        }

    }

    fun displayTime(): String {
        val timeTotalMRT = totalDuration / 60000
        val timeTotalSRT = (totalDuration / 1000) % 60

        maxTime = "${timeTotalMRT}:${String.format("%02d", timeTotalSRT)}"

        return maxTime
    }

    fun displayElapsedTime(): String {
        val currentTimeMRT = currentPosition / 60000
        val currentTimeSRT = (currentPosition / 1000) % 60

        progressTime = "${currentTimeMRT}:${String.format("%02d", currentTimeSRT)}"

        return progressTime
    }

    fun moveFiveSec(time: Int) {
        val mediaPlayer = mediaPlayer ?: return

        mediaPlayer.seekTo(mediaPlayer.currentPosition + time)

        if (mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun moveTo(time: Int) {
        val mediaPlayer = mediaPlayer ?: return

        mediaPlayer.seekTo(time)

        onUpdateTimer?.invoke(mediaPlayer.currentPosition, displayElapsedTime())

        if (mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun configureMediaPlayer(url: String, onReady: ((totalDuration: Int, displayTotalTime: String, displayElapsedTime: String,  mediaPlayer: MediaPlayer) -> Unit)? = null) {
        this.mediaPlayer?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val builder = AudioAttributes.Builder()
                builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                builder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                setAudioAttributes(builder.build())
            } else {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
            }

            isLooping = false

            setDataSource(url)
            setOnPreparedListener { mPlayer ->
                totalDuration = mPlayer.duration

                onReady?.invoke(
                    totalDuration,
                    displayTime(),
                    displayElapsedTime(),
                    mPlayer)
            }
            prepareAsync()
        }
    }

    private fun configureReproducePosition() {
        if (compositeDisposable.isDisposed) { compositeDisposable = CompositeDisposable() }

        compositeDisposable.add(
            Observable.create<Int> { emitter ->

                while (currentPosition < totalDuration) {
                    try {
                        val mediaPlayer = this.mediaPlayer ?: return@create

                        if (mediaPlayer.isPlaying) {

                            currentPosition = mediaPlayer.currentPosition

                            emitter.onNext(currentPosition)

                            if (currentPosition >= totalDuration) {
                                currentPosition = 0
                            }

                            Thread.sleep(1000)
                        }
                    } catch (ex: InterruptedException) {

                    }

                }

                emitter.onComplete()
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentPosition ->
                    this.onUpdateTimer?.invoke(currentPosition, displayElapsedTime())
                }, {
                    this.onError?.invoke("Erro ao configurar o Player", it)
                }, {})
        )
    }
}