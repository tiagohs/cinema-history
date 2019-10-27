package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.helpers.utils.LocaleUtils
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.image.ImageResize
import com.tiagohs.cinema_history.models.image.ImageStyle
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import kotlinx.android.synthetic.main.adapter_movie_info_header.view.*
import kotlin.math.abs

class MovieInfoHeaderViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }?.joinToString(", ") ?: ""
        val writers = movie.credits?.crew?.filter { it.job == "Screenplay" }?.map { it.name }?.joinToString(", ") ?: ""
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