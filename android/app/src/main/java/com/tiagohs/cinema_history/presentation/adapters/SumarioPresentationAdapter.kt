package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import kotlinx.android.synthetic.main.adapter_sumario_presentation_item.view.*

class SumarioPresentationAdapter(
    val context: Context,
    val list: List<com.tiagohs.entities.Sumario>
): RecyclerView.Adapter<SumarioPresentationAdapter.SumarioPresentationViewHolder>() {

    var onSumarioClick: ((sumario: com.tiagohs.entities.Sumario, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumarioPresentationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sumario_presentation_item, parent, false)

        return SumarioPresentationViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SumarioPresentationViewHolder, position: Int) {
        val timelineItem = list[position]

        holder.bind(timelineItem, position)
    }

    inner class SumarioPresentationViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        private var sumario: com.tiagohs.entities.Sumario? = null
        private var positionItem: Int = 0

        fun bind(sumario: com.tiagohs.entities.Sumario, position: Int) {
            this.sumario = sumario
            this.positionItem = position

            itemView.sumarioTitle.text = sumario.title
            itemView.sumarioDescription.text = sumario.description
        }

        override fun onClick(v: View?) {
            val sumario = sumario ?: return

            onSumarioClick?.invoke(sumario, positionItem)
        }

    }
}