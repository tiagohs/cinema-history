package com.tiagohs.cinema_history.presentation.adapters.references

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.references.ReferenceText
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.adapter_reference_media.view.*

class TextViewHolder(
    val context: Context?,
    val view: View
) : RecyclerView.ViewHolder(view) {

    fun bind(referenceBook: ReferenceText) {
        val context = context ?: return

        itemView.mediaName.text = referenceBook.text.styledString()
        itemView.mediaName.setupLinkableTextView(context)

        val link = referenceBook.link
        if (link != null) {
            itemView.mediaContainer.setOnClickListener { context.openLink(link) }
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
