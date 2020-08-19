package com.tiagohs.cinema_history.presentation.adapters.references

import android.content.Context
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceText
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.setResourceStyledText
import kotlinx.android.synthetic.main.adapter_reference_media.*
import kotlinx.android.synthetic.main.adapter_reference_media.view.*

class TextViewHolder(
    val view: View,
    var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<Reference>(view) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val referenceBook = item as? ReferenceText ?: return

        mediaName.setResourceStyledText(referenceBook.text)
        mediaName.setupLinkableTextView(context)

        val link = referenceBook.link
        if (link != null) {
            mediaContainer.setOnClickListener { onLinkClick?.invoke(link) }
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
