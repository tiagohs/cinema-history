package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MovieInfoType
import com.tiagohs.cinema_history.models.movie_info.MovieInfo
import com.tiagohs.cinema_history.models.movie_info.MovieInfoPersonList
import com.tiagohs.cinema_history.ui.adapters.movie_details.MovieInfoHeaderViewHolder
import com.tiagohs.cinema_history.ui.adapters.movie_details.MovieInfoPersonListViewHolder
import com.tiagohs.cinema_history.ui.adapters.movie_details.MovieInfoSummaryViewHolder

class MovieInfoAdapter(
    val context: Context?,
    val list: List<MovieInfo>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onPersonClicked: ((personId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_info_person_list, parent, false)

                MovieInfoPersonListViewHolder(context, view, onPersonClicked)
            }
            MovieInfoType.INFO_HEADER.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_info_header, parent, false)

                MovieInfoHeaderViewHolder(context, view)
            }
            MovieInfoType.INFO_SUMMARY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_info_summary, parent, false)

                MovieInfoSummaryViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_info_summary, parent, false)

                MovieInfoSummaryViewHolder(context, view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> {
                val movieInfo = list[position] as? MovieInfoPersonList ?: return
                val movieInfoPersonViewHolder = holder as? MovieInfoPersonListViewHolder ?: return

                movieInfoPersonViewHolder.bindMovieInfo(movieInfo.listTitle, movieInfo.personList)
            }
            MovieInfoType.INFO_HEADER.ordinal -> {
                val movieInfo = list[position]
                val movieInfoHeaderViewHolder = holder as? MovieInfoHeaderViewHolder ?: return

                movieInfoHeaderViewHolder.bindMovieInfo(movieInfo.movie)
            }
            MovieInfoType.INFO_SUMMARY.ordinal -> {
                val movieInfo = list[position]
                val movieInfoSummaryViewHolder = holder as? MovieInfoSummaryViewHolder ?: return

                movieInfoSummaryViewHolder.bindMovieInfo(movieInfo.movie)
            }
            else -> {
                val movieInfo = list[position]
                val movieInfoSummaryViewHolder = holder as? MovieInfoSummaryViewHolder ?: return

                movieInfoSummaryViewHolder.bindMovieInfo(movieInfo.movie)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }
}