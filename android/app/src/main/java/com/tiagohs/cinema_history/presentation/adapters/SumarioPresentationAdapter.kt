package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Sumario
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_sumario_presentation_item.view.*

class SumarioPresentationAdapter(
    list: List<Sumario>
) : BaseAdapter<Sumario, SumarioPresentationAdapter.SumarioPresentationViewHolder>(list) {

    var onSumarioClick: ((sumario: Sumario, position: Int) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_sumario_presentation_item

    override fun onCreateViewHolder(viewType: Int, view: View): SumarioPresentationViewHolder =
        SumarioPresentationViewHolder(view)

    inner class SumarioPresentationViewHolder(view: View) : BaseViewHolder<Sumario>(view),
        View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun bind(item: Sumario, position: Int) {
            super.bind(item, position)

            itemView.sumarioTitle.setResourceText(item.title)
            itemView.sumarioDescription.setResourceText(item.description)
        }

        override fun onClick(v: View?) {
            val sumario = item ?: return

            onSumarioClick?.invoke(sumario, positionItem)
        }

    }

}