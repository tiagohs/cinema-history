package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.MovieFilmographyDTO
import kotlinx.android.synthetic.main.adapter_movie_item.view.*

class MovieItemAdapter(
    val context: Context?,
    val movies: List<MovieFilmographyDTO>
): RecyclerView.Adapter<MovieItemAdapter.MovieItemViewHolder>() {

    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_item, parent, false)

        return MovieItemViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = movies[position]

        holder.bind(movie)
    }

    inner class MovieItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private var movie: MovieFilmographyDTO? = null

        fun bind(movie: MovieFilmographyDTO) {
            this.movie = movie

            itemView.movieTitle.text = movie.title

            if (!(movie.departments.isNullOrEmpty())) {
                itemView.movieDepartments.visibility = View.VISIBLE
                itemView.movieDepartments.text = "also ${movie.departments}"
            }

            if (!(movie.character.isNullOrEmpty())) {
                itemView.movieCharacter.visibility = View.VISIBLE
                itemView.movieCharacter.text = "as ${movie.character}"
            }

            itemView.image.loadImage(movie.posterPath?.imageUrlFromTMDB(ImageSize.PROFILE_185))
        }

        override fun onClick(v: View?) {
            val movieId = movie?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }
}