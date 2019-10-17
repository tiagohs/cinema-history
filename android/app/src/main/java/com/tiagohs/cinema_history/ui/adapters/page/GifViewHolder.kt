package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.contents.ContentGif
import kotlinx.android.synthetic.main.adapter_page_gif.view.*



class GifViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(gifContent: ContentGif) {
        val context = context ?: return
        val imageThumbnail = gifContent.gifImage.thumbnail

        itemView.videoThumb.loadImage(imageThumbnail)

        itemView.playCard.setOnClickListener {
            itemView.playContainer.visibility = View.INVISIBLE

            val videoView = VideoView(context)
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)

            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

            videoView.id = View.generateViewId()
            videoView.layoutParams = layoutParams

            itemView.videoContainer.addView(videoView)

            when (gifContent.gifImage.imageType) {
                ImageType.LOCAL -> {
                    val backgroundColor = context.resources.getIdentifier(gifContent.gifImage.url, "raw", context.packageName)
                    val uri = Uri.parse("android.resource://" + context.packageName + "/" + backgroundColor)

                    videoView.setVideoURI(uri)
                }
                ImageType.ONLINE -> {
                    videoView.setVideoPath(gifContent.gifImage.url)
                }
            }

            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
            }
        }

        setupContentFooterInformation(gifContent.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_gif
    }
}