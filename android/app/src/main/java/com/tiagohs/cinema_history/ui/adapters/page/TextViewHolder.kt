package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.FontEnum
import com.tiagohs.cinema_history.models.contents.ContentText
import kotlinx.android.synthetic.main.adapter_page_text.view.*

class TextViewHolder(
    val context: Context?,
    val view: View
): RecyclerView.ViewHolder(view) {

    fun bind(content: ContentText) {
        val context = context ?: return

        itemView.contentText.text = content.contentText
        itemView.contentText.typeface = FontEnum.OPEN_SANS_BOLD.getTypeface(context.assets)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_text
    }
}