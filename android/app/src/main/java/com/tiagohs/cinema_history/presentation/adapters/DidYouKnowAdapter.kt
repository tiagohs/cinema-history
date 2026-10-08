package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterDidYouKnowBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.movie.DidYouKnow
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils

class DidYouKnowAdapter(
    list: List<DidYouKnow>
) : BaseAdapter<DidYouKnow, DidYouKnowAdapter.DidYouKnowHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): DidYouKnowHolder =
        DidYouKnowHolder(AdapterDidYouKnowBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    inner class DidYouKnowHolder(private val binding: AdapterDidYouKnowBinding) : BaseViewHolder<DidYouKnow>(binding) {

        override fun bind(item: DidYouKnow, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            binding.didYouKnownTitle.setResourceText(item.title)
            binding.didYouKnownDescription.setResourceText(item.description)

            binding.viewStart.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        }
    }
}