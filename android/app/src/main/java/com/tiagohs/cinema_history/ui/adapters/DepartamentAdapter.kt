package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.getResourceColor
import kotlinx.android.synthetic.main.adapter_department.view.*

class DepartamentAdapter(
    val context: Context?,
    val movies: List<String>,
    val textColor: Int
): RecyclerView.Adapter<DepartamentAdapter.DepartamentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartamentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_department, parent, false)

        return DepartamentViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: DepartamentViewHolder, position: Int) {
        val movie = movies[position]

        holder.bind(movie)
    }

    inner class DepartamentViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(department: String) {
            val context = context ?: return

            if (department.isBlank()) {
                itemView.visibility = View.GONE
                return
            }

            itemView.jobName.text = department
            itemView.jobName.setTextColor(textColor)

            itemView.jobName.background = GradientDrawable().apply {
                setColor(context.getResourceColor(android.R.color.transparent))
                cornerRadius = 5f
                setStroke(1, textColor)
            }

        }

    }
}