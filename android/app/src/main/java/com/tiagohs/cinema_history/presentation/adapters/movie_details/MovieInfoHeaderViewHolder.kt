package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import android.widget.TextView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.DateUtils
import com.tiagohs.helpers.utils.LocaleUtils
import kotlinx.android.synthetic.main.adapter_movie_info_header.*
import kotlinx.android.synthetic.main.adapter_movie_info_header.view.*
import kotlin.math.abs

class MovieInfoHeaderViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie

        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }
            ?.joinToString(", ") ?: ""
        val writers = movie.credits?.crew?.filter { it.job == "Screenplay" || it.job == "Writer" || it.job == "Writing" }
            ?.map { it.name }?.joinToString(", ") ?: ""
        val originalLanguage =
            LocaleUtils.getLanguageName(movie.originalLanguage)?.capitalize() ?: ""
        val releaseDate = DateUtils.formateDate(movie.releaseDate) ?: ""

        bindItem(directors, movieDirectorsTitle, movieDirectors)
        bindItem(writers, movieScreenplayTitle, movieScreenplay)
        bindItem(originalLanguage, originalLanguageTitle, movieOriginalLanguage)
        bindItem(releaseDate, releaseDateTitle, movieReleaseDate)

        bindMoviePoster(movie)
        bindMovieRuntime(movie)
    }

    private fun bindItem(value: String, textViewTitle: TextView, textView: TextView) {
        if (value.isNotBlank()) {
            textViewTitle.show()
            textView.show()
            textView.setResourceText(value)
            textView.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))
        }
    }

    private fun bindMovieRuntime(movie: Movie) {
        val runtime = movie.runtime ?: return
        val hours = abs(runtime / 60)
        val minutes = abs(runtime) % 60

        movieRuntime.setResourceText(containerView.context.getString(R.string.runtime_format, hours, minutes))
        movieRuntime.show()
        runtimeTitle.show()
    }

    private fun bindMoviePoster(movie: Movie) {
        val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_185) ?: return
        val imageStyle = ImageStyle(resize = ImageResize(itemView.width, 180))
        val image = Image(ImageType.ONLINE, imageUrl, imageStyle = imageStyle, contentDescription = containerView.context.getString(R.string.movie_poster_description, movie.originalTitle))

        moviePoster.loadImage(image) {
            imageCard.alpha = 1f
            AnimationUtils.createScaleUpAnimation(
                imageCard,
                0f,
                1f,
                0f,
                1f,
                0.5f,
                0.5f,
                200,
                150
            )
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_header
    }
}