package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentVideo
import com.tiagohs.helpers.extensions.convertIntToDp
import kotlinx.android.synthetic.main.adapter_page_video.*
import kotlinx.android.synthetic.main.adapter_page_video.view.*

class VideoViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val activity = context as? AppCompatActivity ?: return
        val contentVideo = item as? ContentVideo ?: return

        videoViewer.setupPlayer(activity, contentVideo.videoId)

        contentVideo.height?.let {
            videoViewer.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                it.convertIntToDp(context)
            ).apply {
                setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            }
        }

        setupContentFooterInformation(contentVideo.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_video
    }
}