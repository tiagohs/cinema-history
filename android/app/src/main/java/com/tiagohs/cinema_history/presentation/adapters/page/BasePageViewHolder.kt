package com.tiagohs.cinema_history.presentation.adapters.page

import androidx.viewbinding.ViewBinding
import com.tiagohs.cinema_history.databinding.IncludePageContentHeaderBinding
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentInformation
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText

abstract class BasePageViewHolder(binding: ViewBinding) : BaseViewHolder<Content>(binding) {

    open fun onDestroy() {}

    fun setupContentFooterInformation(footer: IncludePageContentHeaderBinding, information: ContentInformation) {
        footer.footerTitle.setResourceText(information.contentTitle)
        footer.footerText.setResourceStyledText(information.contentText)
        footer.footerText.setupLinkableTextView(itemView.context)

        if (information.source.isNullOrEmpty()) {
            footer.footerReference.hide()
            return
        }

        footer.footerReference.setResourceStyledText(information.source)
    }
}
