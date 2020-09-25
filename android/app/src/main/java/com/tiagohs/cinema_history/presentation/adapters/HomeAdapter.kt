package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
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

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        MainTopicsType.HISTORY_CINEMA.ordinal -> R.layout.adapter_home_history_item
        MainTopicsType.TIMELINE.ordinal -> R.layout.adapter_home_timeline_item
        MainTopicsType.DIRECTORS.ordinal -> R.layout.adapter_home_directors_item
        MainTopicsType.AWARDS.ordinal -> R.layout.adapter_home_awards_item
        MainTopicsType.MIL_MOVIES.ordinal -> R.layout.adapter_home_millmovies_item
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): HomeViewHolder =
        HomeViewHolder(view)

    override fun getItemViewType(position: Int): Int = list[position].mainTopicType.ordinal

    inner class HomeViewHolder(view: View) : BaseViewHolder<HomeContentItem>(view) {

        override fun bind(item: HomeContentItem, position: Int) {
            super.bind(item, position)
            val title1 = containerView.findViewById<TextView>(R.id.title1)
            val title2 = containerView.findViewById<TextView>(R.id.title2)
            val startJourney = containerView.findViewById<TextView>(R.id.startJourney)
            val image = containerView.findViewById<ImageView>(R.id.image)
            val card = containerView.findViewById<CardView>(R.id.card)

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