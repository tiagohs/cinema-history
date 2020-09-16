package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentQuote
import com.tiagohs.helpers.extensions.setResourceImageColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.view_quote.*

class QuoteViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentQuote = item as? ContentQuote ?: return

        quoteText.setResourceText(contentQuote.quote.quote)
        quoteTextAuthor.setResourceText(contentQuote.quote.author)

        val colorAsset = ColorUtils.getRandomColorAssets()
        val color = "md_${colorAsset.colorName}_500"

        quoteTop.setResourceImageColor(color)
        quoteBottom.setResourceImageColor(color)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_quote
    }
}