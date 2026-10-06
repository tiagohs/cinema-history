package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageTextBinding
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceFont
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show


class TextViewHolder(
    private val binding: AdapterPageTextBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentTextItem = item as? ContentText ?: return

        setupText(contentTextItem)
        setupTitle(contentTextItem)
        setupCredits(contentTextItem)
    }

    private fun setupText(content: ContentText) {
        val context = itemView.context ?: return
        val text = content.contentText

        if (text != null) {
            binding.contentText.setResourceStyledText(text)
            binding.contentText.setupLinkableTextView(context)
            binding.contentText.setResourceFont(content.font)
            binding.contentText.show()
            return
        }

        binding.contentText.hide()
    }

    private fun setupTitle(contentText: ContentText) {
        val context = itemView.context ?: return
        val title = contentText.contentTitle

        if (title != null) {
            binding.contentTitle.show()
            binding.separator.show()
            binding.contentTitle.setupLinkableTextView(context)
            binding.contentTitle.setResourceStyledText(title)
            return
        }

        binding.contentTitle.hide()
        binding.separator.hide()
    }

    private fun setupCredits(contentText: ContentText) {
        val context = itemView.context ?: return
        val credits = contentText.contentCredits

        if (credits != null) {
            binding.contentCredits.show()
            binding.contentCredits.setupLinkableTextView(context)
            binding.contentCredits.setResourceStyledText(credits)
            return
        }

        binding.contentCredits.hide()
    }
}
