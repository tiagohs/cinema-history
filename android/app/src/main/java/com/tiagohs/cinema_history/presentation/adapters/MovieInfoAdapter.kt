package com.tiagohs.cinema_history.presentation.adapters

import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoWatchOnBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoSummaryBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoReviewsBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoQuoteBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoProductionBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoPersonListBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoMidiaBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoLinkTopicsBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoHeaderBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoDirectorFilmsBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoDidYouKnowBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoCollectionBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoBlockSpecialBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
import androidx.fragment.app.FragmentActivity
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
    var onScreenLink: ((Intent) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<MovieInfo> =
        when (viewType) {
            MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> MovieInfoPersonListViewHolder(AdapterMovieInfoPersonListBinding.inflate(inflater, parent, false), onPersonClicked)
            MovieInfoType.INFO_HEADER.ordinal -> MovieInfoHeaderViewHolder(AdapterMovieInfoHeaderBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_SUMMARY.ordinal -> MovieInfoSummaryViewHolder(AdapterMovieInfoSummaryBinding.inflate(inflater, parent, false), appLanguage, onExtenalLink)
            MovieInfoType.INFO_REVIEWS.ordinal -> MovieInfoReviewsViewHolder(AdapterMovieInfoReviewsBinding.inflate(inflater, parent, false), onExtenalLink, appLanguage)
            MovieInfoType.INFO_DIRECTORS_MOVIE.ordinal -> MovieInfoDirectorFilmsViewHolder(AdapterMovieInfoDirectorFilmsBinding.inflate(inflater, parent, false), activity, onMovieClicked, onPersonClicked)
            MovieInfoType.INFO_QUOTE.ordinal -> MovieInfoQuoteViewHolder(AdapterMovieInfoQuoteBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_COLLECTION.ordinal -> MovieInfoCollectionViewHolder(AdapterMovieInfoCollectionBinding.inflate(inflater, parent, false), appLanguage, onMovieClicked)
            MovieInfoType.INFO_PRODUCTION.ordinal -> MovieInfoProductionViewHolder(AdapterMovieInfoProductionBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_MIDIAS.ordinal -> MovieInfoMidiaViewHolder(AdapterMovieInfoMidiaBinding.inflate(inflater, parent, false), onVideoClick)
            MovieInfoType.INFO_BLOCK_SPECIAL.ordinal -> MovieInfoBlockSpecialViewHolder(AdapterMovieInfoBlockSpecialBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_WATCH_ON.ordinal -> MovieInfoWatchOnViewHolder(AdapterMovieInfoWatchOnBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_DID_YOUT_KNOW.ordinal -> MovieInfoDidYouKnowViewHolder(AdapterMovieInfoDidYouKnowBinding.inflate(inflater, parent, false))
            MovieInfoType.INFO_HISTORY.ordinal -> MovieInfoLinkTopicsViewHolder(AdapterMovieInfoLinkTopicsBinding.inflate(inflater, parent, false), appLanguage, MovieInfoType.INFO_HISTORY, onScreenLink)
            MovieInfoType.INFO_MIL_MOVIES.ordinal -> MovieInfoLinkTopicsViewHolder(AdapterMovieInfoLinkTopicsBinding.inflate(inflater, parent, false), appLanguage, MovieInfoType.INFO_MIL_MOVIES, onScreenLink)

            else -> object : BaseViewHolder<MovieInfo>(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
        }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}