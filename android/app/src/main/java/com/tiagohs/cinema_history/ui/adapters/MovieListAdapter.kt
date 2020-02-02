package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.utils.LocaleUtils
import com.tiagohs.helpers.utils.MovieUtils
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_movie_list.view.*


class MovieListAdapter(
    val context: Context?,
    val list: ArrayList<Movie>,
    val mainTopic: MilMoviesMainTopic
): RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    var onMovieSelected: ((movie: Movie, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_list, parent, false)

        return MovieListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        val movie = list[position]

        holder.bind(movie, position)
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).id?.toLong() ?: position.toLong()
    }

    fun addMoreMovies(movies: List<Movie>) {
        val startPosition = list.size

        list.addAll(movies)

        notifyItemRangeChanged(startPosition, list.size)
    }

    inner class MovieListViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var movie: Movie? = null
        var moviePosition: Int = 0

        fun bind(movie: Movie, position: Int) {
            this.movie = movie
            this.moviePosition = position

            if (position == 0) {
                val context = context ?: return
                val backgroundColorIdentifier = context.resources
                    .getIdentifier(mainTopic.backgroundColor, "color", context.packageName)
                val backgroundColor = context.getResourceColor(backgroundColorIdentifier)
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)

                itemView.backgroundContent.setBackgroundColor(backgroundColor)

                animation.duration = 300
                animation.startOffset = 150

                itemView.backgroundContent.startAnimation(animation)
            } else {
                val color = context?.getResourceColor(R.color.md_white_1000) ?: return

                itemView.backgroundContent.setBackgroundColor(color)

            }

            loadImage(movie)

            itemView.title.text = movie.title ?: movie.originalTitle
            itemView.originalTitle.text = movie.originalTitle

            val lang = LocaleUtils.getLanguageName(movie.originalLanguage)
            if (movie.originalLanguage != null && lang != null) {
                itemView.language.text = lang.capitalize()
            } else {
                itemView.language.visibility = View.GONE
            }

            val genreList = MovieUtils.getGenresName(context, movie.genreIds)
            if (genreList.isNotEmpty()) {
                itemView.genre.text = genreList.first()
            } else {
                itemView.genre.visibility = View.GONE
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
            val movie = this.movie ?: return

            onMovieSelected?.invoke(movie, moviePosition)
        }
    }
}