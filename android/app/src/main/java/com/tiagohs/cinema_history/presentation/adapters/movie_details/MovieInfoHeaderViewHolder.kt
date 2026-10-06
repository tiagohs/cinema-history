package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoHeaderBinding
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
import kotlin.math.abs

class MovieInfoHeaderViewHolder(
    private val binding: AdapterMovieInfoHeaderBinding
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie

        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }
            ?.joinToString(", ") ?: ""
        val writers = movie.credits?.crew?.filter { it.job == "Screenplay" || it.job == "Writer" || it.job == "Writing" }
            ?.map { it.name }?.joinToString(", ") ?: ""
        val originalLanguage =
            LocaleUtils.getLanguageName(movie.originalLanguage)?.replaceFirstChar { it.titlecase() } ?: ""
        val releaseDate = DateUtils.formateDate(movie.releaseDate) ?: ""

        bindItem(directors, binding.movieDirectorsTitle, binding.movieDirectors)
        bindItem(writers, binding.movieScreenplayTitle, binding.movieScreenplay)
        bindItem(originalLanguage, binding.originalLanguageTitle, binding.movieOriginalLanguage)
        bindItem(releaseDate, binding.releaseDateTitle, binding.movieReleaseDate)

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

        binding.movieRuntime.setResourceText(itemView.context.getString(R.string.runtime_format, hours, minutes))
        binding.movieRuntime.show()
        binding.runtimeTitle.show()
    }

    private fun bindMoviePoster(movie: Movie) {
        val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_185) ?: return
        val imageStyle = ImageStyle(resize = ImageResize(itemView.width, 180))
        val image = Image(ImageType.ONLINE, imageUrl, imageStyle = imageStyle, contentDescription = itemView.context.getString(R.string.movie_poster_description, movie.originalTitle))

        binding.moviePoster.loadImage(image) {
            binding.imageCard.alpha = 1f
            AnimationUtils.createScaleUpAnimation(
                binding.imageCard,
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
}