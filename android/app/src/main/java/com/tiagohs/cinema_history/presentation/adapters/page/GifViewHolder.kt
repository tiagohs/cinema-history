package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageGifBinding
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentGif

class GifViewHolder(
    private val binding: AdapterPageGifBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)

        val gifContent = item as? ContentGif ?: return
        val imageThumbnail = gifContent.gifImage.thumbnail

        binding.gifViewer.setupGif(gifContent.gifImage, imageThumbnail)
        setupContentFooterInformation(binding.footerContainer, gifContent.information)
    }

    override fun onDestroy() {
        binding.gifViewer.onDestroy()

        super.onDestroy()
    }
}