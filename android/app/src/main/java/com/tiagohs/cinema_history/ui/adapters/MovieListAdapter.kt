package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.utils.LocaleUtils
import com.tiagohs.cinema_history.helpers.utils.MovieUtils
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.models.tmdb.Movie
import kotlinx.android.synthetic.main.adapter_movie_list.view.*

class MovieListAdapter(
    val context: Context?,
    val list: List<Movie>,
    val mainTopic: MilMoviesMainTopic
): RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

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

    inner class MovieListViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie, position: Int) {

            if (position == 0) {
                val context = context ?: return
                val backgroundColor = context.resources
                    .getIdentifier(mainTopic.backgroundColor, "color", context.packageName)
                itemView.backgroundContent.setBackgroundColor(context.resources.getColor(backgroundColor))
            } else {
                val color = context?.resources?.getColor(R.color.md_white_1000) ?: return

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

        }

        private fun loadImage(movie: Movie) {
            val url = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return

            itemView.image.loadImage(url)
        }
    }
}