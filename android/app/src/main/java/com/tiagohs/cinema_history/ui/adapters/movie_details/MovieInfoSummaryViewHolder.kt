package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import kotlinx.android.synthetic.main.adapter_movie_info_summary.view.*


class MovieInfoSummaryViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val overview = movie.overview ?: "Esse filme não possui uma sinopse disponível."

        itemView.movieSummary.text = overview
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_summary
    }
}