package com.tiagohs.cinema_history.presentation.adapters.config

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<T>(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

    var item: T? = null
    var positionItem = 0

    open fun bind(item: T, position: Int) {
        this.item = item
        this.positionItem = position
    }
}