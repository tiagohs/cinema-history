package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentInformation
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.include_page_content_header.*

abstract class BasePageViewHolder(view: View) : BaseViewHolder<Content>(view) {

    open fun onDestroy() {}

    fun setupContentFooterInformation(information: ContentInformation) {
        footerTitle?.setResourceText(information.contentTitle)
        footerText?.setResourceStyledText(information.contentText)

        if (information.source.isNullOrEmpty()) {
            footerReference.hide();
            return
        }

        footerReference?.setResourceStyledText(information.source)
    }
}