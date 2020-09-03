package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.movie_details.*
import com.tiagohs.entities.enums.MovieInfoType
import com.tiagohs.entities.movie_info.MovieInfo

class MovieInfoAdapter(
    list: List<MovieInfo>,
    private val activity: FragmentActivity,
    private val appLanguage: String
) : BaseAdapter<MovieInfo, BaseViewHolder<MovieInfo>>(list) {

    var onPersonClicked: ((personId: Int) -> Unit)? = null
    var onVideoClick: ((String?) -> Unit)? = null
    var onExtenalLink: ((String?) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> MovieInfoPersonListViewHolder.LAYOUT_ID
        MovieInfoType.INFO_HEADER.ordinal -> MovieInfoHeaderViewHolder.LAYOUT_ID
        MovieInfoType.INFO_SUMMARY.ordinal -> MovieInfoSummaryViewHolder.LAYOUT_ID
        MovieInfoType.INFO_REVIEWS.ordinal -> MovieInfoReviewsViewHolder.LAYOUT_ID
        MovieInfoType.INFO_COLLECTION.ordinal -> MovieInfoCollectionViewHolder.LAYOUT_ID
        MovieInfoType.INFO_DIRECTORS_MOVIE.ordinal -> MovieInfoDirectorFilmsViewHolder.LAYOUT_ID
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
            MovieInfoType.INFO_SUMMARY.ordinal -> MovieInfoSummaryViewHolder(view, appLanguage, onExtenalLink)
            MovieInfoType.INFO_REVIEWS.ordinal -> MovieInfoReviewsViewHolder(view, onExtenalLink, appLanguage)
            MovieInfoType.INFO_DIRECTORS_MOVIE.ordinal -> MovieInfoDirectorFilmsViewHolder(view, activity, onMovieClicked, onPersonClicked)
            MovieInfoType.INFO_COLLECTION.ordinal -> MovieInfoCollectionViewHolder(view, appLanguage, onMovieClicked)
            MovieInfoType.INFO_PRODUCTION.ordinal -> MovieInfoProductionViewHolder(view)
            MovieInfoType.INFO_MIDIAS.ordinal -> MovieInfoMidiaViewHolder(view, onVideoClick)
            else -> object : BaseViewHolder<MovieInfo>(view) {}
        }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}