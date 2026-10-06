package com.tiagohs.cinema_history.presentation.adapters.references

import com.tiagohs.cinema_history.databinding.AdapterReferenceMediaBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceMedia
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText

class MediaViewHolder(
    private val binding: AdapterReferenceMediaBinding,
    var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<Reference>(binding) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val referenceBook = item as? ReferenceMedia ?: return

        binding.mediaName.setResourceText(referenceBook.title)
        binding.mediaAuthor.setResourceText(referenceBook.subtitle)
        binding.mediaDescription.setResourceText(referenceBook.description)
        // mediaType é um código fixo no JSON ("Livro", "Série", "Filme", "Youtube"), igual em todos os idiomas.
        binding.mediaType.text = mediaTypeLabel(referenceBook.mediaType)
        binding.mediaImage.loadImage(referenceBook.image)

        binding.mediaContainer.setOnClickListener { onClickListener(referenceBook) }
        binding.buyButon.setOnClickListener { onClickListener(referenceBook) }

        val color = when (referenceBook.mediaType) {
            "Livro" -> R.color.md_red_500
            "Série" -> R.color.md_deep_orange_500
            "Filme" -> R.color.md_purple_500
            "Youtube" -> R.color.md_green_500
            else -> R.color.colorAccent
        }
        binding.mediaType.setResourceBackgroundColor(color)
        binding.buyButon.setCardBackgroundColor(itemView.context.getResourceColor(color))

        val buttonText = referenceBook.buttonText
        if (buttonText != null) {
            binding.buyButonText.setResourceText(buttonText)
            return
        }

        binding.buyButonText.setResourceText(R.string.buy)
    }

    private fun onClickListener(referenceMedia: ReferenceMedia) {
        onLinkClick?.invoke(referenceMedia.link)
    }

    private fun mediaTypeLabel(code: String?): String? {
        val res = when (code) {
            "Livro" -> R.string.reference_media_type_book
            "Série" -> R.string.reference_media_type_series
            "Filme" -> R.string.reference_media_type_movie
            "Youtube" -> R.string.reference_media_type_youtube
            else -> return code
        }
        return itemView.context.getString(res)
    }
}
