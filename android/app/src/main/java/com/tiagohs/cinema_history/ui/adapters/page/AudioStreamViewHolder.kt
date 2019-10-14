package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.contents.ContentAudioStream
import kotlinx.android.synthetic.main.adapter_page_audio_stream.view.*

class AudioStreamViewHolder(
        val context: Context?,
        val view: View
): BasePageViewHolder(view) {

    fun bind(contentAudioStream: ContentAudioStream) {
        val context = context ?: return

        itemView.audioView.setAudioImage(contentAudioStream.image)
        itemView.audioView.setAudioUrl(contentAudioStream.path)
        itemView.audioView.prepare()

        setupContentFooterInformation(contentAudioStream.information)
    }

    override fun onDestroy() {
        itemView.audioView
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_audio_stream
    }
}