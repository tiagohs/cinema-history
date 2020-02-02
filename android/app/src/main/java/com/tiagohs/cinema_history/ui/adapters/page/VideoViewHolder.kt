package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.entities.contents.ContentVideo
import kotlinx.android.synthetic.main.adapter_page_video.view.*

class VideoViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentVideo: ContentVideo) {
        val context = context ?: return
        val activity = context as? AppCompatActivity ?: return

        itemView.videoViewer.setupPlayer(activity, contentVideo.videoId)

        contentVideo.height?.let {
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))
            params.setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            itemView.videoViewer.layoutParams = params
        }

        setupContentFooterInformation(contentVideo.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_video
    }
}