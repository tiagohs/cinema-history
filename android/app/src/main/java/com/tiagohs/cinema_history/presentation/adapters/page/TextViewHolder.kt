package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceFont
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_page_text.*


class TextViewHolder(
    view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentTextItem = item as? ContentText ?: return

        contentText.setResourceStyledText(contentTextItem.contentText)
        contentText.setupLinkableTextView(context)
        contentText.setResourceFont(contentTextItem.font)

        setupTitle(contentTextItem)
        setupCredits(contentTextItem)
    }

    private fun setupTitle(contentText: ContentText) {
        val context = containerView.context ?: return
        val title = contentText.contentTitle

        if (title != null) {
            contentTitle.show()
            separator.show()
            contentTitle.setupLinkableTextView(context)
            contentTitle.setResourceStyledText(title)
            return
        }

        contentTitle.hide()
        separator.hide()
    }

    private fun setupCredits(contentText: ContentText) {
        val context = containerView.context ?: return
        val credits = contentText.contentCredits

        if (credits != null) {
            contentCredits.show()
            contentCredits.setupLinkableTextView(context)
            contentCredits.setResourceStyledText(credits)
            return
        }

        contentCredits.hide()
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_text
    }
}
