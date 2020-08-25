package com.tiagohs.cinema_history.presentation.adapters.references

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceText
import com.tiagohs.helpers.extensions.setResourceStyledText
import kotlinx.android.synthetic.main.adapter_reference_text.*

class TextViewHolder(
    val view: View
) : BaseViewHolder<Reference>(view) {

    override fun bind(item: Reference, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val referenceText = item as? ReferenceText ?: return

        textReference.setResourceStyledText(referenceText.text)
        textReference.setupLinkableTextView(context)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_text
    }
}
