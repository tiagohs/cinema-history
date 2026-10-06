package com.tiagohs.cinema_history.presentation.adapters

import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterDepartmentBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceTextColor

class DepartamentAdapter(
    list: List<String>,
    val textColor: Int
) : BaseAdapter<String, DepartamentAdapter.DepartamentViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): DepartamentViewHolder =
        DepartamentViewHolder(AdapterDepartmentBinding.inflate(inflater, parent, false))

    inner class DepartamentViewHolder(private val binding: AdapterDepartmentBinding) : BaseViewHolder<String>(binding) {

        override fun bind(item: String, position: Int) {
            super.bind(item, position)

            if (item.isBlank()) {
                itemView.hide()
                return
            }

            binding.jobName.setResourceText(item)
            binding.jobName.setTextColor(textColor)

            binding.jobName.background = GradientDrawable().apply {
                setColor(itemView.context.getResourceColor(android.R.color.transparent))
                cornerRadius = 5f
                setStroke(1, textColor)
            }
        }
    }

}