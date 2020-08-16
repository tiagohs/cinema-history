package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.core.content.ContextCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentQuote
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_page_quote.view.*

class QuoteViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentQuote = item as? ContentQuote ?: return

        itemView.quoteText.setResourceText(contentQuote.quote.quote)
        itemView.quoteTextAuthor.setResourceText(contentQuote.quote.author)

        val quoteMarkColor = contentQuote.quoteMarkColor
        if (quoteMarkColor != null) {
            bindQuoteColor(context.getResourceColor(quoteMarkColor))
            return
        }

        bindQuoteColor(context.getResourceColor(R.color.md_white_1000))
    }

    private fun bindQuoteColor(color: Int) {
        val topDrawable = itemView.quoteTop.drawable
        val bottomDrawable = itemView.quoteBottom.drawable

        topDrawable.setTint(color)
        bottomDrawable.setTint(color)

        itemView.quoteTop.setImageDrawable(topDrawable)
        itemView.quoteBottom.setImageDrawable(bottomDrawable)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_quote
    }
}