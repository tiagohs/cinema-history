package com.tiagohs.uicomponents.videoviewer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.show
import com.tiagohs.uicomponents.databinding.ViewPlayContainerBinding
import com.tiagohs.uicomponents.databinding.ViewVideoViewerBinding

class VideoViewerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    lateinit var youtubePlayerView: YouTubePlayerView

    private lateinit var activity: AppCompatActivity
    private lateinit var videoId: String

    private val binding = ViewVideoViewerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupPlayer(activity: AppCompatActivity, videoId: String) {
        this.activity = activity
        this.videoId = videoId

        setupPlayContainer()
    }

    private fun loadVideoThumbnail(playContainer: ViewPlayContainerBinding) {
        val vieoThumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"
        val image = Image(ImageType.ONLINE, url = vieoThumbnailUrl, imageStyle = ImageStyle(scaleType = "center_crop"))

        playContainer.videoThumb.loadImage(image) {
            playContainer.playCard.show()
            playContainer.loadCard.hide()
        }
    }

    private fun setupPlayContainer() {
        binding.videoContainer.removeAllViews()

        val playContainerBinding = ViewPlayContainerBinding.inflate(LayoutInflater.from(context), null, false)

        playContainerBinding.root.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            bottomToBottom = LayoutParams.PARENT_ID
            topToTop = LayoutParams.PARENT_ID
            startToStart = LayoutParams.PARENT_ID
            endToEnd = LayoutParams.PARENT_ID
        }

        playContainerBinding.playCard.setOnClickListener {
            playContainerBinding.playContainer.hide()

            setupYoutubeViewPlayerView()
            loadVideo()
        }

        loadVideoThumbnail(playContainerBinding)

        binding.videoContainer.addView(playContainerBinding.root)
    }

    private fun setupYoutubeViewPlayerView() {
        youtubePlayerView = YouTubePlayerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomToBottom = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            }

            id = View.generateViewId()
        }

        binding.videoContainer.addView(youtubePlayerView)
    }

    private fun loadVideo() {
        activity.lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }



}