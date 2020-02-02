package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.DateUtils
import com.tiagohs.helpers.utils.LocaleUtils
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import kotlinx.android.synthetic.main.adapter_movie_info_header.view.*
import kotlin.math.abs

class MovieInfoHeaderViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }?.joinToString(", ") ?: ""
        val writers = movie.credits?.crew?.filter { it.job == "Screenplay" || it.job == "Writer" }?.map { it.name }?.joinToString(", ") ?: ""
        val originalLanguage = LocaleUtils.getLanguageName(movie.originalLanguage)?.capitalize() ?: ""
        val releaseDate = DateUtils.formateDate(movie.releaseDate) ?: ""

        bindItem(directors, itemView.movieDirectorsTitle, itemView.movieDirectors)
        bindItem(writers, itemView.movieScreenplayTitle, itemView.movieScreenplay)
        bindItem(originalLanguage, itemView.originalLanguageTitle, itemView.movieOriginalLanguage)
        bindItem(releaseDate, itemView.releaseDateTitle, itemView.movieReleaseDate)

        bindMoviePoster(movie)
        bindMovieRuntime(movie)
    }

    private fun bindItem(value: String, textViewTitle: TextView, textView: TextView) {
        if (value.isNotBlank()) {
            textViewTitle.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE

            textView.text = value

            textView.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))
        }
    }

    private fun bindMovieRuntime(movie: Movie) {
        val runtime = movie.runtime ?: return
        val hours = abs(runtime / 60 )
        val minutes = abs(runtime) % 60
        val runtimeFormated = "${hours}h ${minutes}m"

        itemView.movieRuntime.text = runtimeFormated

        itemView.movieRuntime.visibility = View.VISIBLE
        itemView.runtimeTitle.visibility = View.VISIBLE
    }

    private fun bindMoviePoster(movie: Movie) {
        val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_185) ?: return
        val imageStyle = ImageStyle(resize = ImageResize(itemView.width, 180))
        val image = Image(ImageType.ONLINE, imageUrl, imageStyle = imageStyle)

        itemView.moviePoster.loadImage(image) {
            itemView.imageCard.alpha = 1f
            AnimationUtils.createScaleUpAnimation(itemView.imageCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200, 150)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_header
    }
}