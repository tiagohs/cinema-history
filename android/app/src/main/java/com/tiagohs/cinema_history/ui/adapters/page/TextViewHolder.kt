package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.core.text.HtmlCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentText
import kotlinx.android.synthetic.main.adapter_page_text.view.*

class TextViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentText: ContentText) {
        val context = context ?: return

        itemView.contentText.text = contentText.contentText.styledString()

        contentText.font?.let {
            val font = Typeface.createFromAsset(context.assets, "fonts/${it}.ttf")
            itemView.contentText.typeface = font

        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_text
    }
}