package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.cinema_history.presentation.adapters.movie_details.*
import com.tiagohs.entities.enums.MovieInfoType

class MovieInfoAdapter(
    val context: Context?,
    val list: List<MovieInfo>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onPersonClicked: ((personId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MovieInfoType.INFO_CAST.ordinal, MovieInfoType.INFO_CREW.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoPersonListViewHolder.LAYOUT_ID, parent, false)

                MovieInfoPersonListViewHolder(context, view, onPersonClicked)
            }
            MovieInfoType.INFO_HEADER.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoHeaderViewHolder.LAYOUT_ID, parent, false)

                MovieInfoHeaderViewHolder(context, view)
            }
            MovieInfoType.INFO_SUMMARY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoSummaryViewHolder.LAYOUT_ID, parent, false)

                MovieInfoSummaryViewHolder(context, view)
            }
            MovieInfoType.INFO_PRODUCTION.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoProductionViewHolder.LAYOUT_ID, parent, false)

                MovieInfoProductionViewHolder(context, view)
            }
            MovieInfoType.INFO_MIDIAS.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoMidiaViewHolder.LAYOUT_ID, parent, false)

                MovieInfoMidiaViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(MovieInfoSummaryViewHolder.LAYOUT_ID, parent, false)

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
            MovieInfoType.INFO_PRODUCTION.ordinal -> {
                val movieInfo = list[position]
                val movieInfoProductionViewHolder = holder as? MovieInfoProductionViewHolder ?: return

                movieInfoProductionViewHolder.bindMovieInfo(movieInfo.movie)
            }
            MovieInfoType.INFO_MIDIAS.ordinal -> {
                val movieInfo = list[position]
                val movieInfoMidiaViewHolder = holder as? MovieInfoMidiaViewHolder ?: return

                movieInfoMidiaViewHolder.bindMovieInfo(movieInfo.movie)
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