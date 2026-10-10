package com.tiagohs.cinema_history.presentation.adapters.awards

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.databinding.AdapterAwardYearPillBinding
import com.tiagohs.entities.awards.AwardYearSummary

/** Seletor rápido de anos (pílulas horizontais, ~100 anos no Oscar). */
class AwardYearAdapter(
    private val onYearClicked: (position: Int, summary: AwardYearSummary) -> Unit
) : ListAdapter<AwardYearSummary, AwardYearAdapter.YearHolder>(DIFF) {

    var selectedYear: String? = null
        private set

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).year.hashCode().toLong()

    fun select(year: String?) {
        if (year == selectedYear) return
        val old = indexOf(selectedYear)
        selectedYear = year
        if (old >= 0) notifyItemChanged(old, PAYLOAD_SELECTION)
        val new = indexOf(year)
        if (new >= 0) notifyItemChanged(new, PAYLOAD_SELECTION)
    }

    fun indexOf(year: String?): Int {
        year ?: return -1
        for (i in 0 until itemCount) if (getItem(i).year == year) return i
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearHolder =
        YearHolder(AdapterAwardYearPillBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: YearHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_SELECTION)) {
            holder.bindSelection(getItem(position))
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: YearHolder, position: Int) {
        val item = getItem(position)
        holder.binding.yearPill.text = item.year
        holder.bindSelection(item)
    }

    inner class YearHolder(val binding: AdapterAwardYearPillBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.yearPill.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) onYearClicked(position, getItem(position))
            }
        }

        fun bindSelection(item: AwardYearSummary) {
            binding.yearPill.isSelected = item.year == selectedYear
        }
    }

    /** Rola suavemente até deixar o item no centro da lista. */
    class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int =
            (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
            60f / displayMetrics.densityDpi
    }

    companion object {
        private const val PAYLOAD_SELECTION = "selection"

        private val DIFF = object : DiffUtil.ItemCallback<AwardYearSummary>() {
            override fun areItemsTheSame(oldItem: AwardYearSummary, newItem: AwardYearSummary) = oldItem.year == newItem.year
            override fun areContentsTheSame(oldItem: AwardYearSummary, newItem: AwardYearSummary) = oldItem == newItem
        }
    }
}
