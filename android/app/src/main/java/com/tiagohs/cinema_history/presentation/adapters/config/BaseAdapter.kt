package com.tiagohs.cinema_history.presentation.adapters.config

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<I, V : BaseViewHolder<I>>(
        var list: List<I>
): RecyclerView.Adapter<V>() {

    abstract fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): V

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V =
        onCreateViewHolder(viewType, LayoutInflater.from(parent.context), parent)

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: V, position: Int) {
        if (list.isEmpty()) { return }

        holder.bind(list[position], position)
    }

}
