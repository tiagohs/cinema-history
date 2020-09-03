package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_home_directors_item.*
import kotlinx.android.synthetic.main.adapter_home_history_item.*
import kotlinx.android.synthetic.main.adapter_home_millmovies_item.*
import kotlinx.android.synthetic.main.adapter_home_millmovies_item.homeMillMoviesItemCard
import kotlinx.android.synthetic.main.adapter_home_timeline_item.*

class HomeAdapter(
    list: List<HomeContentItem>
) : BaseAdapter<HomeContentItem, HomeAdapter.HomeViewHolder>(list) {

    var onItemClicked: ((HomeContentItem) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        MainTopicsType.HISTORY_CINEMA.ordinal -> R.layout.adapter_home_history_item
        MainTopicsType.TIMELINE.ordinal -> R.layout.adapter_home_timeline_item
        MainTopicsType.DIRECTORS.ordinal -> R.layout.adapter_home_directors_item
        MainTopicsType.MIL_MOVIES.ordinal -> R.layout.adapter_home_millmovies_item
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): HomeViewHolder =
        HomeViewHolder(view)

    override fun getItemViewType(position: Int): Int = list[position].mainTopicType.ordinal

    inner class HomeViewHolder(view: View) : BaseViewHolder<HomeContentItem>(view) {

        override fun bind(item: HomeContentItem, position: Int) {
            super.bind(item, position)
            val historyLogoTitle1 = containerView.findViewById<TextView>(R.id.title1)
            val historyLogoTitle2 = containerView.findViewById<TextView>(R.id.title2)
            val image = containerView.findViewById<ImageView>(R.id.image)

            when (item.mainTopicType) {
                MainTopicsType.HISTORY_CINEMA -> {
                    historyLogoTitle1
                        .animate()
                        .alpha(1f)
                        .setStartDelay(200)
                        .setDuration(300)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()

                    historyLogoTitle2
                        .animate()
                        .alpha(1f)
                        .setDuration(400)
                        .setStartDelay(400)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withEndAction { image.loadImage(item.image, null) }
                        .start()

                    homeItemCard.setOnClickListener { onItemClicked?.invoke(item) }
                }
                MainTopicsType.TIMELINE -> {
                    bindItem(item, image, homeTimelineItemCard)
                }
                MainTopicsType.MIL_MOVIES -> {
                    bindItem(item, image, homeMillMoviesItemCard)
                }
                MainTopicsType.DIRECTORS -> {
                    bindItem(item, image, homeDirectorsItemCard)
                }
                else -> { }
            }
        }

        private fun bindItem(
            item: HomeContentItem,
            imageView: ImageView,
            itemContainerCard: CardView
        ) {
            imageView.loadImage(item.image, null)

            itemContainerCard.setOnClickListener { onItemClicked?.invoke(item) }
        }
    }

}