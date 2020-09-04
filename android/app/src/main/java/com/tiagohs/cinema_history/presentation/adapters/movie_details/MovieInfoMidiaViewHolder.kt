package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieVideoAdapter
import com.tiagohs.cinema_history.presentation.adapters.MovieWallpaperAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_movie_info_midia.*
import kotlinx.android.synthetic.main.adapter_movie_info_midia.view.*


class MovieInfoMidiaViewHolder(
    view: View,
    var onVideoClick: ((String?) -> Unit)? = null
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val context = containerView.context

        bindImages(context, movie)
        bindVideos(context, movie)
    }

    private fun bindImages(context: Context, movie: Movie) {
        val allImages = movie.allImages ?: emptyList()

        wallpapersList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MovieWallpaperAdapter(allImages, movie.originalTitle)
            addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.LEFT
                )
            )
        }
    }

    private fun bindVideos(context: Context, movie: Movie) {
        val allVideos = movie.videos?.videoList ?: emptyList()

        videoList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MovieVideoAdapter(allVideos, movie).apply {
                onVideoClick = this@MovieInfoMidiaViewHolder.onVideoClick
            }
            addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.LEFT
                )
            )
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_midia
    }
}