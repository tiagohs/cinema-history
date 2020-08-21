package com.tiagohs.uicomponents.gifviewer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.GifImage
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.show
import com.tiagohs.uicomponents.R
import kotlinx.android.synthetic.main.view_gif_viewer_view.view.*
import kotlinx.android.synthetic.main.view_play_container.view.*

class GifViewerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    lateinit var videoView: VideoView
    lateinit var gifImage: GifImage

    var videoId: Int = 0

    init {
        View.inflate(context, R.layout.view_gif_viewer_view, this)
    }

    fun setupGif(gifImage: GifImage, gifThumbnail: Image) {
        this.gifImage = gifImage

        setupPlayContainer(gifThumbnail)
    }

    fun onDestroy() {
        videoView.stopPlayback()
    }

    private fun loadVideoThumbnail(playContainerView: View, gifThumbnail: Image?) {
        val thumbnail = gifThumbnail ?: return

        playContainerView.videoThumb.loadImage(thumbnail) {
            playContainerView.playCard.show()
            playContainerView.loadCard.hide()
        }
    }

    private fun setupPlayContainer(gifThumbnail: Image) {
        gifContainer.removeAllViews()

        val playContainerView = LayoutInflater.from(context).inflate(R.layout.view_play_container, null, false).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomToBottom = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
            }

            this.playCard.setOnClickListener {
                this.playContainer.hide()

                setupVideoView()
                loadGif()
            }

            loadVideoThumbnail(this, gifThumbnail)
        }

        gifContainer.addView(playContainerView)
    }

    private fun setupVideoView() {
        videoView = VideoView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomToBottom = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            }

            videoId = View.generateViewId()
            id = videoId
        }

        gifContainer.addView(videoView)
    }

    private fun loadGif() {
        when (gifImage.imageType) {
            ImageType.LOCAL -> {
                val backgroundColor = context.resources.getIdentifier(gifImage.url, "raw", context.packageName)
                val uri = Uri.parse("android.resource://" + context.packageName + "/" + backgroundColor)

                videoView.setVideoURI(uri)
            }
            ImageType.ONLINE -> {
                videoView.setVideoPath(gifImage.url)
            }
            else -> return
        }

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.start()
        }
    }

}