package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.ContentQuote
import kotlinx.android.synthetic.main.adapter_page_quote.view.*

class QuoteViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentQuote: ContentQuote) {
        val context = context ?: return

        itemView.quoteText.text = contentQuote.quote.quote
        itemView.quoteTextAuthor.text = contentQuote.quote.author

        contentQuote.quoteMarkColor?.let {
            val topDrawable = itemView.quoteTop.drawable
            val bottomDrawable = itemView.quoteBottom.drawable
            val color = context.resources
                .getIdentifier(it, "color", context.packageName)

            topDrawable.setTint(ContextCompat.getColor(context, color))
            bottomDrawable.setTint(ContextCompat.getColor(context, color))

            itemView.quoteTop.setImageDrawable(topDrawable)
            itemView.quoteBottom.setImageDrawable(bottomDrawable)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_quote
    }
}