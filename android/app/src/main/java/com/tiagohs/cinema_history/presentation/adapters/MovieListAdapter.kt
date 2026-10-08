package com.tiagohs.cinema_history.presentation.adapters

import android.R.attr.radius
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieListBinding
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.LocaleUtils
import com.tiagohs.helpers.utils.MovieUtils


class MovieListAdapter(
    list: ArrayList<Movie>,
    val activity: Activity,
    val mainTopic: MilMoviesMainTopic
) : BaseAdapter<Movie, MovieListAdapter.MovieListViewHolder>(list) {

    var onMovieSelected: ((movie: Movie, position: Int) -> Unit)? = null
    var onLoadBackdrop: ((url: String?) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieListViewHolder =
        MovieListViewHolder(AdapterMovieListBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long =
        list.get(position).id?.toLong() ?: position.toLong()

    fun addMoreMovies(movies: List<Movie>) {
        val startPosition = list.size

        val list = list.toMutableList()
        list.addAll(movies)
        this.list = list

        notifyItemRangeChanged(startPosition, list.size)
    }

    inner class MovieListViewHolder(private val binding: AdapterMovieListBinding) : BaseViewHolder<Movie>(binding),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var moviePosition: Int = 0

        override fun bind(item: Movie, position: Int) {
            super.bind(item, position)
            val context = itemView.context ?: return
            this.moviePosition = position

            if (position == 0) {
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)

                binding.backgroundContent.setResourceBackgroundColor(mainTopic.backgroundColor)

                animation.duration = 300
                animation.startOffset = 150

                binding.backgroundContent.startAnimation(animation)
            } else {
                binding.backgroundContent.setResourceBackgroundColor(R.color.transparent)
            }

            loadImage(item)

            binding.title.setResourceText(item.title ?: item.originalTitle)
            binding.originalTitle.setResourceText(item.originalTitle)

            val lang = LocaleUtils.getLanguageName(item.originalLanguage)
            if (item.originalLanguage != null && lang != null) {
                binding.language.setResourceText(lang.replaceFirstChar { it.titlecase() })
            } else {
                binding.language.hide()
            }

            val genreList = MovieUtils.getGenresName(context, item.genreIds)
            if (genreList.isNotEmpty()) {
                binding.genre.setResourceText(genreList.firstOrNull())
            } else {
                binding.genre.hide()
            }

            com.tiagohs.helpers.utils.AnimationUtils
                .createScaleUpAnimation(binding.languageCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200, 150)
            com.tiagohs.helpers.utils.AnimationUtils
                .createScaleUpAnimation(binding.genreCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200, 150)

        }

        private fun loadImage(movie: Movie) {
            val url = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return

            binding.image.loadImage(url)

            onLoadBackdrop?.invoke(url)
        }

        override fun onClick(v: View?) {
            val movie = item ?: return

            onMovieSelected?.invoke(movie, moviePosition)
        }
    }

}