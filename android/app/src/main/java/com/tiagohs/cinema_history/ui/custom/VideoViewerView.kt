package com.tiagohs.cinema_history.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.image.ImageStyle
import kotlinx.android.synthetic.main.view_video_viewer.view.*
import kotlinx.android.synthetic.main.view_video_viewer.view.playCard

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

        playCard.setOnClickListener {
            playContainer.visibility = View.GONE

            setupYoutubeViewPlayerView()
            loadVideo()
        }

        loadVideoThumbnail()
    }

    private fun loadVideoThumbnail() {
        val vieoThumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"
        val image = Image(ImageType.ONLINE, vieoThumbnailUrl, imageStyle = ImageStyle(scaleType = "center_crop"))

        videoThumb.loadImage(image) {
            playCard.visibility = View.VISIBLE
            loadCard.visibility = View.GONE
        }
    }

    private fun setupYoutubeViewPlayerView() {
        youtubePlayerView = YouTubePlayerView(context)

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        layoutParams.topToTop = LayoutParams.PARENT_ID
        layoutParams.startToStart = LayoutParams.PARENT_ID
        layoutParams.endToEnd = LayoutParams.PARENT_ID
        layoutParams.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)

        youtubePlayerView.id = View.generateViewId()
        youtubePlayerView.layoutParams = layoutParams

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