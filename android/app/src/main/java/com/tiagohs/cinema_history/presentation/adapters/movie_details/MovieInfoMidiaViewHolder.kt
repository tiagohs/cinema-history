package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.cinema_history.presentation.adapters.MovieVideoAdapter
import com.tiagohs.cinema_history.presentation.adapters.MovieWallpaperAdapter
import kotlinx.android.synthetic.main.adapter_movie_info_midia.view.*


class MovieInfoMidiaViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        bindImages(movie)
        bindVideos(movie)
    }

    private fun bindImages(movie: Movie) {
        val allImages = ArrayList<Image>(movie.images?.backdrops ?: emptyList())
        allImages.addAll(movie.images?.posters ?: emptyList())

        itemView.wallpapersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.wallpapersList.adapter = MovieWallpaperAdapter(context, allImages)
        itemView.wallpapersList.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.LEFT))
    }

    private fun bindVideos(movie: Movie) {
        val allVideos = movie.videos?.videoList ?: emptyList()

        itemView.videoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.videoList.adapter = MovieVideoAdapter(context, allVideos)
        itemView.videoList.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.LEFT))
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_midia
    }
}