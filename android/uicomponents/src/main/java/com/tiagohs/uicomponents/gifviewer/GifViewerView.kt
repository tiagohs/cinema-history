package com.tiagohs.uicomponents.gifviewer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.GifImage
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.uicomponents.R
import kotlinx.android.synthetic.main.view_gif_viewer_view.view.*

class GifViewerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    lateinit var videoView: VideoView
    lateinit var gifImage: GifImage

    init {
        View.inflate(context, R.layout.view_gif_viewer_view, this)
    }

    fun setupGif(gifImage: GifImage, gifThumbnail: Image) {
        this.gifImage = gifImage

        playCard.setOnClickListener {
            playContainer.visibility = View.GONE

            setupVideoView()
            loadGif()
        }

        loadVideoThumbnail(gifThumbnail)
    }

    private fun loadVideoThumbnail(gifThumbnail: Image?) {
        val thumbnail = gifThumbnail ?: return

        videoThumb.loadImage(thumbnail) {
            playCard.visibility = View.VISIBLE
            loadCard.visibility = View.GONE
        }
    }


    private fun setupVideoView() {
        videoView = VideoView(context)

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        layoutParams.topToTop = LayoutParams.PARENT_ID
        layoutParams.startToStart = LayoutParams.PARENT_ID
        layoutParams.endToEnd = LayoutParams.PARENT_ID
        layoutParams.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)

        videoView.id = View.generateViewId()
        videoView.layoutParams = layoutParams

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