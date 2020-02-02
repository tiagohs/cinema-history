package com.tiagohs.cinema_history.ui.adapters.page

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.entities.contents.ContentInformation
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.include_page_content_header.view.*

abstract class BasePageViewHolder(view: View): RecyclerView.ViewHolder(view) {

    open fun onDestroy() {}

    fun setupContentFooterInformation(information: ContentInformation) {
        itemView.footerTitle?.text = information.contentTitle
        itemView.footerText?.text = information.contentText?.styledString()

        if (information.source.isNullOrEmpty()) { itemView.footerReference.visibility = View.GONE; return }

        itemView.footerReference?.text = information.source?.styledString()
    }
}