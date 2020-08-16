package com.tiagohs.cinema_history.presentation.adapters

import android.graphics.drawable.GradientDrawable
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceTextColor
import kotlinx.android.synthetic.main.adapter_department.view.*

class DepartamentAdapter(
    list: List<String>,
    val textColor: Int
) : BaseAdapter<String, DepartamentAdapter.DepartamentViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_department

    override fun onCreateViewHolder(viewType: Int, view: View): DepartamentViewHolder =
        DepartamentViewHolder(view)

    inner class DepartamentViewHolder(view: View) : BaseViewHolder<String>(view) {

        override fun bind(item: String, position: Int) {
            super.bind(item, position)

            if (item.isBlank()) {
                itemView.hide()
                return
            }

            itemView.jobName.setResourceText(item)
            itemView.jobName.setTextColor(textColor)

            itemView.jobName.background = GradientDrawable().apply {
                setColor(containerView.context.getResourceColor(android.R.color.transparent))
                cornerRadius = 5f
                setStroke(1, textColor)
            }
        }
    }

}