package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.Sumario

class SumarioPresentationAdapter(
    val context: Context,
    val list: List<Sumario>
): RecyclerView.Adapter<SumarioPresentationAdapter.SumarioPresentationViewHolder>() {

    var onSumarioClick: ((sumario: Sumario) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumarioPresentationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sumario_presentation_item, parent, false)

        return SumarioPresentationViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SumarioPresentationViewHolder, position: Int) {
        val timelineItem = list[position]

        holder.bind(timelineItem)
    }

    inner class SumarioPresentationViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        private var sumario: Sumario? = null

        fun bind(sumario: Sumario) {
            this.sumario = sumario
        }

        override fun onClick(v: View?) {
            val sumario = sumario ?: return

            onSumarioClick?.invoke(sumario)
        }

    }
}