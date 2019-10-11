package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.Sumario
import kotlinx.android.synthetic.main.adapter_sumario_item.view.*

class SumarioAdapter(
    val context: Context?,
    val list: List<Sumario>
): RecyclerView.Adapter<SumarioAdapter.SumarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sumario_item, parent, false)

        return SumarioViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SumarioViewHolder, position: Int) {
        val timelineItem = list[position]

        holder.bind(timelineItem)
    }

    inner class SumarioViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(sumario: Sumario) {
            val context = context ?: return

            itemView.sumarioItemTitle.text = "Pioneiros do Cinema"
        }

    }
}