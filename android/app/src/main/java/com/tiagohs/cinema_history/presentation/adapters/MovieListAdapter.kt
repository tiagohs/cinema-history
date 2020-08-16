package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
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
import kotlinx.android.synthetic.main.adapter_movie_list.view.*

class MovieListAdapter(
    list: ArrayList<Movie>,
    val mainTopic: MilMoviesMainTopic
) : BaseAdapter<Movie, MovieListAdapter.MovieListViewHolder>(list) {

    var onMovieSelected: ((movie: Movie, position: Int) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_list

    override fun onCreateViewHolder(viewType: Int, view: View): MovieListViewHolder =
        MovieListViewHolder(view)

    override fun getItemId(position: Int): Long =
        list.get(position).id?.toLong() ?: position.toLong()

    fun addMoreMovies(movies: List<Movie>) {
        val startPosition = list.size

        val list = list.toMutableList()
        list.addAll(movies)
        this.list = list

        notifyItemRangeChanged(startPosition, list.size)
    }

    inner class MovieListViewHolder(view: View) : BaseViewHolder<Movie>(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var moviePosition: Int = 0

        override fun bind(item: Movie, position: Int) {
            super.bind(item, position)
            val context = containerView.context ?: return
            this.moviePosition = position

            if (position == 0) {
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)

                itemView.backgroundContent.setResourceBackgroundColor(mainTopic.backgroundColor)

                animation.duration = 300
                animation.startOffset = 150

                itemView.backgroundContent.startAnimation(animation)
            } else {
                itemView.backgroundContent.setResourceBackgroundColor(R.color.md_white_1000)
            }

            loadImage(item)

            itemView.title.setResourceText(item.title ?: item.originalTitle)
            itemView.originalTitle.setResourceText(item.originalTitle)

            val lang = LocaleUtils.getLanguageName(item.originalLanguage)
            if (item.originalLanguage != null && lang != null) {
                itemView.language.setResourceText(lang.capitalize())
            } else {
                itemView.language.hide()
            }

            val genreList = MovieUtils.getGenresName(context, item.genreIds)
            if (genreList.isNotEmpty()) {
                itemView.genre.setResourceText(genreList.firstOrNull())
            } else {
                itemView.genre.hide()
            }

            com.tiagohs.helpers.utils.AnimationUtils
                .createScaleUpAnimation(itemView.languageCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200, 150)
            com.tiagohs.helpers.utils.AnimationUtils
                .createScaleUpAnimation(itemView.genreCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200, 150)

        }

        private fun loadImage(movie: Movie) {
            val url = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return

            itemView.image.loadImage(url)
        }

        override fun onClick(v: View?) {
            val movie = item ?: return

            onMovieSelected?.invoke(movie, moviePosition)
        }
    }

}