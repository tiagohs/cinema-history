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
import com.tiagohs.uicomponents.databinding.ViewGifViewerViewBinding
import com.tiagohs.uicomponents.databinding.ViewPlayContainerBinding

class GifViewerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    lateinit var videoView: VideoView
    lateinit var gifImage: GifImage

    var videoId: Int = 0

    private val binding = ViewGifViewerViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupGif(gifImage: GifImage, gifThumbnail: Image) {
        this.gifImage = gifImage

        setupPlayContainer(gifThumbnail)
    }

    fun onDestroy() {
        videoView.stopPlayback()
    }

    private fun loadVideoThumbnail(playContainer: ViewPlayContainerBinding, gifThumbnail: Image?) {
        val thumbnail = gifThumbnail ?: return

        playContainer.videoThumb.loadImage(thumbnail) {
            playContainer.playCard.show()
            playContainer.loadCard.hide()
        }
    }

    private fun setupPlayContainer(gifThumbnail: Image) {
        binding.gifContainer.removeAllViews()

        val playContainerBinding = ViewPlayContainerBinding.inflate(LayoutInflater.from(context), null, false)

        playContainerBinding.root.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            bottomToBottom = LayoutParams.PARENT_ID
            topToTop = LayoutParams.PARENT_ID
            startToStart = LayoutParams.PARENT_ID
            endToEnd = LayoutParams.PARENT_ID
        }

        playContainerBinding.playCard.setOnClickListener {
            playContainerBinding.playContainer.hide()

            setupVideoView()
            loadGif()
        }

        loadVideoThumbnail(playContainerBinding, gifThumbnail)

        binding.gifContainer.addView(playContainerBinding.root)
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

        binding.gifContainer.addView(videoView)
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