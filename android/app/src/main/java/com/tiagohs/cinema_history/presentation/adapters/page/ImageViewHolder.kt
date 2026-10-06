package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageImgBinding
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentImage
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setupPreview

class ImageViewHolder(
    private val binding: AdapterPageImgBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentImage = item as? ContentImage ?: return

        binding.image.loadImage(contentImage.image)
        binding.image.setupPreview(contentImage.image)

        setupContentFooterInformation(binding.footerContainer, contentImage.information)
    }
}