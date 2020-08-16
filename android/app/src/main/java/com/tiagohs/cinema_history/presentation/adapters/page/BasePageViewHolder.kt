package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentInformation
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.include_page_content_header.view.*

abstract class BasePageViewHolder(view: View): BaseViewHolder<Content>(view) {

    open fun onDestroy() {}

    fun setupContentFooterInformation(information: ContentInformation) {
        itemView.footerTitle?.setResourceText(information.contentTitle)
        itemView.footerText?.setResourceStyledText(information.contentText)

        if (information.source.isNullOrEmpty()) {
            itemView.footerReference.hide();
            return
        }

        itemView.footerReference?.setResourceStyledText(information.source)
    }
}