package com.tiagohs.cinema_history.presentation.adapters.references

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.references.ReferenceBook
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.openLink
import kotlinx.android.synthetic.main.adapter_reference_media.view.*

class MediaViewHolder(
    val context: Context?,
    val view: View
): RecyclerView.ViewHolder(view) {

    fun bind(referenceBook: ReferenceBook) {
        itemView.mediaName.text = referenceBook.title
        itemView.mediaAuthor.text = referenceBook.subtitle
        itemView.mediaDescription.text = referenceBook.description
        itemView.mediaType.text = referenceBook.mediaType

        itemView.mediaImage.loadImage(referenceBook.image)

        itemView.mediaContainer.setOnClickListener { onClickListener(referenceBook) }
        itemView.buyButon.setOnClickListener { onClickListener(referenceBook)  }
    }

    private fun onClickListener(referenceBook: ReferenceBook) {
        val context = context ?: return

        context.openLink(referenceBook.link)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
