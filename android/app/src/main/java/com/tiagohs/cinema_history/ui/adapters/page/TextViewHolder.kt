package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.graphics.Typeface
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.adapter_page_text.view.*


class TextViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentText: ContentText) {
        val context = context ?: return

        itemView.contentText.text = contentText.contentText.styledString()
        itemView.contentText.setupLinkableTextView(context)

        contentText.font?.let {
            val font = Typeface.createFromAsset(context.assets, "fonts/${it}.ttf")
            itemView.contentText.typeface = font
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_text
    }
}
