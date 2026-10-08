package com.tiagohs.cinema_history.presentation.adapters.references

import com.tiagohs.cinema_history.databinding.AdapterReferenceTextBinding
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceText
import com.tiagohs.helpers.extensions.setResourceStyledText

class TextViewHolder(
    private val binding: AdapterReferenceTextBinding
) : BaseViewHolder<Reference>(binding) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val referenceText = item as? ReferenceText ?: return

        binding.textReference.setResourceStyledText(referenceText.text)
        binding.textReference.setupLinkableTextView(context)
    }
}
