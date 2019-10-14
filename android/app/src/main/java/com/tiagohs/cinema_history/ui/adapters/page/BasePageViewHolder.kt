package com.tiagohs.cinema_history.ui.adapters.page

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentInformation
import kotlinx.android.synthetic.main.include_page_content_header.view.*

abstract class BasePageViewHolder(view: View): RecyclerView.ViewHolder(view) {

    open fun onDestroy() {}

    fun setupContentFooterInformation(information: ContentInformation) {
        itemView.footerTitle?.text = information.contentTitle
        itemView.footerText?.text = information.contentText.styledString()
        itemView.footerReference?.text = information.source.styledString()
    }
}