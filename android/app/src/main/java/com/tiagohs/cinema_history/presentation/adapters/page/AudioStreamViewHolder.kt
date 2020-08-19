package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentAudioStream
import kotlinx.android.synthetic.main.adapter_page_audio_stream.*

class AudioStreamViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentAudioStream = item as? ContentAudioStream ?: return

        audioView.setAudioImage(contentAudioStream.image)
        audioView.setAudioUrl(contentAudioStream.path)
        audioView.prepare()

        setupContentFooterInformation(contentAudioStream.information)
    }

    override fun onDestroy() {
        audioView.onDestroy()
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_audio_stream
    }
}