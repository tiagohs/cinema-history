package com.tiagohs.cinema_history.ui.adapters.page

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentText
import com.tiagohs.cinema_history.models.contents.ContentVideo
import kotlinx.android.synthetic.main.adapter_page_video.view.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp

class VideoViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentVideo: ContentVideo) {
        val context = context ?: return
        val activity = context as? AppCompatActivity

        activity?.lifecycle?.addObserver(itemView.videoContentView)

        itemView.videoContentView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = contentVideo.videoId

                youTubePlayer.cueVideo(videoId, 0f)
            }
        })

        contentVideo.height?.let {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))
            params.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            itemView.videoContentView.layoutParams = params
        }

        setupContentFooterInformation(contentVideo.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_video
    }
}