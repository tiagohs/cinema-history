package com.tiagohs.cinema_history.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
import com.tiagohs.cinema_history.databinding.AdapterHomeAwardsItemBinding
import com.tiagohs.cinema_history.databinding.AdapterHomeDirectorsItemBinding
import com.tiagohs.cinema_history.databinding.AdapterHomeHistoryItemBinding
import com.tiagohs.cinema_history.databinding.AdapterHomeMillmoviesItemBinding
import com.tiagohs.cinema_history.databinding.AdapterHomeTimelineItemBinding
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.helpers.extensions.loadImage

class HomeAdapter(
    list: List<HomeContentItem>
) : BaseAdapter<HomeContentItem, HomeAdapter.HomeViewHolder>(list) {

    var onItemClicked: ((HomeContentItem) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): HomeViewHolder =
        HomeViewHolder(
            when (viewType) {
                MainTopicsType.HISTORY_CINEMA.ordinal -> AdapterHomeHistoryItemBinding.inflate(inflater, parent, false)
                MainTopicsType.TIMELINE.ordinal -> AdapterHomeTimelineItemBinding.inflate(inflater, parent, false)
                MainTopicsType.DIRECTORS.ordinal -> AdapterHomeDirectorsItemBinding.inflate(inflater, parent, false)
                MainTopicsType.AWARDS.ordinal -> AdapterHomeAwardsItemBinding.inflate(inflater, parent, false)
                MainTopicsType.MIL_MOVIES.ordinal -> AdapterHomeMillmoviesItemBinding.inflate(inflater, parent, false)
                else -> AdapterEmptyBinding.inflate(inflater, parent, false)
            }
        )

    override fun getItemViewType(position: Int): Int = list[position].mainTopicType.ordinal

    inner class HomeViewHolder(binding: ViewBinding) : BaseViewHolder<HomeContentItem>(binding) {

        override fun bind(item: HomeContentItem, position: Int) {
            super.bind(item, position)
            val title1 = itemView.findViewById<TextView>(R.id.title1)
            val title2 = itemView.findViewById<TextView>(R.id.title2)
            val startJourney = itemView.findViewById<TextView>(R.id.startJourney)
            val image = itemView.findViewById<ImageView>(R.id.image)
            val card = itemView.findViewById<CardView>(R.id.card)

            bindItem(item, title1, title2, startJourney, image, card)
        }

        private fun bindItem(
            item: HomeContentItem,
            title1: TextView?,
            title2: TextView?,
            startJourney: TextView?,
            imageView: ImageView?,
            itemContainerCard: CardView?
        ) {
            title1
                ?.animate()
                ?.alpha(1f)
                ?.setStartDelay(200)
                ?.setDuration(300)
                ?.setInterpolator(AccelerateDecelerateInterpolator())
                ?.start()

            title2
                ?.animate()
                ?.alpha(1f)
                ?.setDuration(400)
                ?.setStartDelay(400)
                ?.setInterpolator(AccelerateDecelerateInterpolator())
                ?.withEndAction {
                    imageView?.loadImage(item.image, null).apply {
                        startJourney?.animate()
                            ?.alpha(1f)
                            ?.setStartDelay(200)
                            ?.setDuration(300)
                            ?.setInterpolator(AccelerateDecelerateInterpolator())
                            ?.start()
                    }
                }
                ?.start()

            itemContainerCard?.setOnClickListener { onItemClicked?.invoke(item) }
        }
    }

}