package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.helpers.utils.LocaleUtils
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import kotlinx.android.synthetic.main.adapter_movie_info_header.view.*
import kotlin.math.abs

class MovieInfoHeaderViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }?.joinToString(", ") ?: "--"
        val writers = movie.credits?.crew?.filter { it.job == "Screenplay" }?.map { it.name }?.joinToString(", ") ?: "--"
        val originalLanguage = LocaleUtils.getLanguageName(movie.originalLanguage)?.capitalize() ?: "--"

        itemView.movieDirectors.text = directors
        itemView.movieScreenplay.text = writers
        itemView.movieOriginalLanguage.text = originalLanguage
        itemView.movieReleaseDate.text = DateUtils.formateDate(movie.releaseDate)

        bindMoviePoster(movie)
        bindMovieRuntime(movie)
    }

    private fun bindMovieRuntime(movie: Movie) {
        val runtime = movie.runtime ?: return
        val hours = abs(runtime / 60 )
        val minutes = abs(runtime) % 60
        val runtimeFormated = "${hours}h ${minutes}m"

        itemView.movieRuntime.text = runtimeFormated
    }

    private fun bindMoviePoster(movie: Movie) {
        val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return

        Picasso.get()
            .load(imageUrl)
            .centerCrop()
            .resize(itemView.width, 180.convertIntToDp(context))
            .into(itemView.moviePoster)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_header
    }
}