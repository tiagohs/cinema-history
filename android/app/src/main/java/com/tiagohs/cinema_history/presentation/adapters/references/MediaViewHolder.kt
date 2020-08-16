package com.tiagohs.cinema_history.presentation.adapters.references

import android.content.Context
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceBook
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_reference_media.view.*

class MediaViewHolder(
    val view: View,
    var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<Reference>(view) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val referenceBook = item as? ReferenceBook ?: return

        itemView.mediaName.setResourceText(referenceBook.title)
        itemView.mediaAuthor.setResourceText(referenceBook.subtitle)
        itemView.mediaDescription.setResourceText(referenceBook.description)
        itemView.mediaType.setResourceText(referenceBook.mediaType)
        itemView.mediaImage.loadImage(referenceBook.image)

        itemView.mediaContainer.setOnClickListener { onClickListener(referenceBook) }
        itemView.buyButon.setOnClickListener { onClickListener(referenceBook) }
    }

    private fun onClickListener(referenceBook: ReferenceBook) {
        onLinkClick?.invoke(referenceBook.link)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
