package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.movie.DidYouKnow
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_did_you_know.*

class DidYouKnowAdapter(
    list: List<DidYouKnow>
) : BaseAdapter<DidYouKnow, DidYouKnowAdapter.DidYouKnowHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_did_you_know

    override fun onCreateViewHolder(viewType: Int, view: View): DidYouKnowHolder =
        DidYouKnowHolder(view)

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    inner class DidYouKnowHolder(view: View) : BaseViewHolder<DidYouKnow>(view) {

        override fun bind(item: DidYouKnow, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            didYouKnownTitle.setResourceText(item.title)
            didYouKnownDescription.setResourceText(item.description)

            viewStart.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        }
    }
}