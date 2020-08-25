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
import com.tiagohs.uicomponents.R
import kotlinx.android.synthetic.main.view_play_container.view.*
import kotlinx.android.synthetic.main.view_video_viewer.view.*

class VideoViewerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    lateinit var youtubePlayerView: YouTubePlayerView

    private lateinit var activity: AppCompatActivity
    private lateinit var videoId: String

    init {
        View.inflate(context, R.layout.view_video_viewer, this)
    }

    fun setupPlayer(activity: AppCompatActivity, videoId: String) {
        this.activity = activity
        this.videoId = videoId

        setupPlayContainer()
    }

    private fun loadVideoThumbnail(playContainerView: View) {
        val vieoThumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"
        val image = Image(ImageType.ONLINE, url = vieoThumbnailUrl, imageStyle = ImageStyle(scaleType = "center_crop"))

        playContainerView.videoThumb.loadImage(image) {
            playContainerView.playCard.show()
            playContainerView.loadCard.hide()
        }
    }

    private fun setupPlayContainer() {
        videoContainer.removeAllViews()

        val playContainerView = LayoutInflater.from(context).inflate(R.layout.view_play_container, null, false).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomToBottom = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
            }

            this.playCard.setOnClickListener {
                this.playContainer.hide()

                setupYoutubeViewPlayerView()
                loadVideo()
            }

            loadVideoThumbnail(this)
        }

        videoContainer.addView(playContainerView)
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

        videoContainer.addView(youtubePlayerView)
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