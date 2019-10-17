package com.tiagohs.cinema_history.ui.adapters.page

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentText
import com.tiagohs.cinema_history.models.contents.ContentVideo
import kotlinx.android.synthetic.main.adapter_page_video.view.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.image.ImageStyle
import kotlinx.android.synthetic.main.adapter_page_video.view.playCard
import kotlinx.android.synthetic.main.adapter_page_video.view.playContainer
import kotlinx.android.synthetic.main.adapter_page_video.view.videoContainer

class VideoViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentVideo: ContentVideo) {
        val context = context ?: return
        val activity = context as? AppCompatActivity

        itemView.videoThumb.loadImage(Image(
            ImageType.ONLINE,
            "https://img.youtube.com/vi/${contentVideo.videoId}/0.jpg",
            imageStyle = ImageStyle(scaleType = "center_crop")
        ))

        itemView.playCard.setOnClickListener {
            itemView.playContainer.visibility = View.INVISIBLE

            val youtubeView = YouTubePlayerView(context)
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)

            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)

            youtubeView.id = View.generateViewId()
            youtubeView.layoutParams = layoutParams

            itemView.videoContainer.addView(youtubeView)

            activity?.lifecycle?.addObserver(youtubeView)

            youtubeView.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val videoId = contentVideo.videoId

                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
        }

        contentVideo.height?.let {
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))
            params.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            itemView.videoContainer.layoutParams = params
        }

        setupContentFooterInformation(contentVideo.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_video
    }
}