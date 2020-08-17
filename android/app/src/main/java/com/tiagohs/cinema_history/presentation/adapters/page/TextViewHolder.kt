package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Context
import android.graphics.Typeface
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_page_text.view.*


class TextViewHolder(
    view: View
): BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentText = item as? ContentText ?: return

        itemView.contentText.setResourceStyledText(contentText.contentText)
        itemView.contentText.setupLinkableTextView(context)
        itemView.contentText.setResourceFont(contentText.font)

        setupTitle(contentText)
    }

    private fun setupTitle(contentText: ContentText) {
        val context = containerView.context ?: return
        val title = contentText.contentTitle

        if (title != null) {
            itemView.contentTitle.show()
            itemView.separator.show()
            itemView.contentTitle.setupLinkableTextView(context)
            itemView.contentTitle.setResourceStyledText(title)
            return
        }

        itemView.contentTitle.hide()
        itemView.separator.hide()
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_text
    }
}
