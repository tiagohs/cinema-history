package com.tiagohs.cinema_history.presentation.adapters.config

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T>(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    var item: T? = null
    var positionItem = 0

    open fun bind(item: T, position: Int) {
        this.item = item
        this.positionItem = position
    }
}
