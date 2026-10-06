package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageAudioStreamBinding
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentAudioStream

class AudioStreamViewHolder(
    private val binding: AdapterPageAudioStreamBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentAudioStream = item as? ContentAudioStream ?: return

        binding.audioView.setAudioImage(contentAudioStream.image)
        binding.audioView.setAudioUrl(contentAudioStream.path)
        binding.audioView.prepare()

        setupContentFooterInformation(binding.footerContainer, contentAudioStream.information)
    }

    override fun onDestroy() {
        binding.audioView.onDestroy()
    }
}