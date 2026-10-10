package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageVideoBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentVideo
import com.tiagohs.helpers.extensions.convertIntToDp

class VideoViewHolder(
    private val binding: AdapterPageVideoBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val activity = context as? AppCompatActivity ?: return
        val contentVideo = item as? ContentVideo ?: return

        binding.videoViewer.setupPlayer(activity, contentVideo.videoId)

        contentVideo.height?.let {
            binding.videoViewer.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                // tablets: altura proporcional à coluna de mídia mais larga (1.0 no celular)
                com.tiagohs.cinema_history.presentation.configs.LargeScreen.scaledHeightPx(context, it)
            ).apply {
                setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 0)
            }
        }

        setupContentFooterInformation(binding.footerContainer, contentVideo.information)
    }
}