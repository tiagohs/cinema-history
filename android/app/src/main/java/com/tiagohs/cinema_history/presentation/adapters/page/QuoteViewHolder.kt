package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentQuote
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceBackgroundTint
import com.tiagohs.helpers.extensions.setResourceImageColor
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_page_quote.*

class QuoteViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentQuote = item as? ContentQuote ?: return

        quoteText.setResourceText(contentQuote.quote.quote)
        quoteTextAuthor.setResourceText(contentQuote.quote.author)

        val quoteMarkColor = contentQuote.quoteMarkColor
        if (quoteMarkColor != null) {
            quoteTop.setResourceImageColor(quoteMarkColor)
            quoteBottom.setResourceImageColor(quoteMarkColor)
            return
        }

        quoteTop.setResourceImageColor(R.color.md_white_1000)
        quoteBottom.setResourceImageColor(R.color.md_white_1000)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_quote
    }
}