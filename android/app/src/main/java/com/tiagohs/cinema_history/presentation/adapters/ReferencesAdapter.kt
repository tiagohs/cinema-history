package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterReferenceTextBinding
import com.tiagohs.cinema_history.databinding.AdapterReferenceMediaBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
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

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<Reference> =
        when (viewType) {
            ReferenceType.MEDIA.ordinal -> MediaViewHolder(AdapterReferenceMediaBinding.inflate(inflater, parent, false), onLinkClick)
            ReferenceType.TEXT.ordinal -> TextViewHolder(AdapterReferenceTextBinding.inflate(inflater, parent, false))
            else -> object : BaseViewHolder<Reference>(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
        }

    override fun getItemViewType(position: Int): Int = list.get(position).type.ordinal
}