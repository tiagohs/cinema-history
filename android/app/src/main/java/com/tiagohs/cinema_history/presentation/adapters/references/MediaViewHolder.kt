package com.tiagohs.cinema_history.presentation.adapters.references

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceMedia
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_reference_media.*

class MediaViewHolder(
    val view: View,
    var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<Reference>(view) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val referenceBook = item as? ReferenceMedia ?: return

        mediaName.setResourceText(referenceBook.title)
        mediaAuthor.setResourceText(referenceBook.subtitle)
        mediaDescription.setResourceText(referenceBook.description)
        mediaType.setResourceText(referenceBook.mediaType)
        mediaImage.loadImage(referenceBook.image)

        mediaContainer.setOnClickListener { onClickListener(referenceBook) }
        buyButon.setOnClickListener { onClickListener(referenceBook) }

        val color = when (referenceBook.mediaType) {
            "Livro" -> R.color.md_red_500
            "Série" -> R.color.md_deep_orange_500
            "Filme" -> R.color.md_purple_500
            "Youtube" -> R.color.md_brown_500
            else -> R.color.colorAccent
        }
        mediaType.setResourceBackgroundColor(color)
        buyButon.setCardBackgroundColor(containerView.context.getResourceColor(color))
        
        val buttonText = referenceBook.buttonText
        if (buttonText != null) {
            buyButonText.setResourceText(buttonText)
            return
        }

        buyButonText.setResourceText(R.string.buy)
    }

    private fun onClickListener(referenceMedia: ReferenceMedia) {
        onLinkClick?.invoke(referenceMedia.link)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_media
    }
}
