package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageQuoteBinding
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentQuote
import com.tiagohs.helpers.extensions.setResourceImageColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.utils.ColorUtils

class QuoteViewHolder(
    private val binding: AdapterPageQuoteBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentQuote = item as? ContentQuote ?: return

        binding.viewQuote.quoteText.setResourceStyledText(contentQuote.quote.quote)
        binding.viewQuote.quoteTextAuthor.setResourceStyledText(contentQuote.quote.author)

        val colorAsset = ColorUtils.getRandomColorAssets()
        val color = "md_${colorAsset.colorName}_500"

        binding.viewQuote.quoteTop.setResourceImageColor(color)
        binding.viewQuote.quoteBottom.setResourceImageColor(color)
    }
}