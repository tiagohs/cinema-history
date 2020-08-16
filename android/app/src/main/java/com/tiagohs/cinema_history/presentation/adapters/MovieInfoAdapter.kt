package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.movie_details.*
import com.tiagohs.entities.enums.MovieInfoType
import com.tiagohs.entities.movie_info.MovieInfo

class MovieInfoAdapter(
    list: List<MovieInfo>
) : BaseAdapter<MovieInfo, BaseViewHolder<MovieInfo>>(list) {

    var onPersonClicked: ((personId: Int) -> Unit)? = null
    var onVideoClick: ((String?) -> Unit)? = null
    var onExtenalLink: ((String?) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> MovieInfoPersonListViewHolder.LAYOUT_ID
        MovieInfoType.INFO_HEADER.ordinal -> MovieInfoHeaderViewHolder.LAYOUT_ID
        MovieInfoType.INFO_SUMMARY.ordinal -> MovieInfoSummaryViewHolder.LAYOUT_ID
        MovieInfoType.INFO_PRODUCTION.ordinal -> MovieInfoProductionViewHolder.LAYOUT_ID
        MovieInfoType.INFO_MIDIAS.ordinal -> MovieInfoMidiaViewHolder.LAYOUT_ID
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BaseViewHolder<MovieInfo> =
        when (viewType) {
            MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> MovieInfoPersonListViewHolder(
                view,
                onPersonClicked
            )
            MovieInfoType.INFO_HEADER.ordinal -> MovieInfoHeaderViewHolder(view)
            MovieInfoType.INFO_SUMMARY.ordinal -> MovieInfoSummaryViewHolder(view, onExtenalLink)
            MovieInfoType.INFO_PRODUCTION.ordinal -> MovieInfoProductionViewHolder(view)
            MovieInfoType.INFO_MIDIAS.ordinal -> MovieInfoMidiaViewHolder(view, onVideoClick)
            else -> object : BaseViewHolder<MovieInfo>(view) {}
        }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}