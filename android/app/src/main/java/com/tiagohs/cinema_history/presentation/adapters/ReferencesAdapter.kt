package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.references.MediaViewHolder
import com.tiagohs.cinema_history.presentation.adapters.references.TextViewHolder
import com.tiagohs.entities.enums.ReferenceType
import com.tiagohs.entities.references.Reference

class ReferencesAdapter(
    list: List<Reference>
) : BaseAdapter<Reference, BaseViewHolder<Reference>>(list) {

    var onLinkClick: ((String?) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        ReferenceType.MEDIA.ordinal -> MediaViewHolder.LAYOUT_ID
        ReferenceType.TEXT.ordinal -> TextViewHolder.LAYOUT_ID
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BaseViewHolder<Reference> =
        when (viewType) {
            ReferenceType.MEDIA.ordinal -> MediaViewHolder(view, onLinkClick)
            ReferenceType.TEXT.ordinal -> TextViewHolder(view)
            else -> object : BaseViewHolder<Reference>(view) {}
        }

    override fun getItemViewType(position: Int): Int = list.get(position).type.ordinal
}