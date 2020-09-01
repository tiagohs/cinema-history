package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.widget.ImageView
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

            when (item.mainTopicType) {
                MainTopicsType.HISTORY_CINEMA -> {
                    bindItem(item, historyMovieImage, homeItemCard)
                }
                MainTopicsType.TIMELINE -> {
                    bindItem(item, timelineImage, homeTimelineItemCard)
                }
                MainTopicsType.MIL_MOVIES -> {
                    bindItem(item, moviesImage, homeMillMoviesItemCard)
                }
                MainTopicsType.DIRECTORS -> {
                    bindItem(item, directorsImage, homeDirectorsItemCard)
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