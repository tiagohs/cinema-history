package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterSumarioPresentationItemBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Sumario
import com.tiagohs.helpers.extensions.setResourceText

class SumarioPresentationAdapter(
    list: List<Sumario>
) : BaseAdapter<Sumario, SumarioPresentationAdapter.SumarioPresentationViewHolder>(list) {

    var onSumarioClick: ((sumario: Sumario, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): SumarioPresentationViewHolder =
        SumarioPresentationViewHolder(AdapterSumarioPresentationItemBinding.inflate(inflater, parent, false))

    inner class SumarioPresentationViewHolder(private val binding: AdapterSumarioPresentationItemBinding) : BaseViewHolder<Sumario>(binding),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: Sumario, position: Int) {
            super.bind(item, position)

            binding.sumarioTitle.setResourceText(item.title)
            binding.sumarioDescription.setResourceText(item.description)
        }

        override fun onClick(v: View?) {
            val sumario = item ?: return

            onSumarioClick?.invoke(sumario, positionItem)
        }

    }

}