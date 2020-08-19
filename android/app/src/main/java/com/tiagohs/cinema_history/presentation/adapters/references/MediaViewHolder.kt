package com.tiagohs.cinema_history.presentation.adapters.references

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceBook
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_reference_media.*

class MediaViewHolder(
    val view: View,
    var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<Reference>(view) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val referenceBook = item as? ReferenceBook ?: return

        mediaName.setResourceText(referenceBook.title)
        mediaAuthor.setResourceText(referenceBook.subtitle)
        mediaDescription.setResourceText(referenceBook.description)
        mediaType.setResourceText(referenceBook.mediaType)
        mediaImage.loadImage(referenceBook.image)

        mediaContainer.setOnClickListener { onClickListener(referenceBook) }
        buyButon.setOnClickListener { onClickListener(referenceBook) }
    }

    private fun onClickListener(referenceBook: ReferenceBook) {
        onLinkClick?.invoke(referenceBook.link)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
