package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieItemCollectionBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText

class MovieItemCollectionAdapter(
    list: List<Movie>,
    val appLanguage: String,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null
) : BaseAdapter<Movie, MovieItemCollectionAdapter.MovieItemCollectionViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieItemCollectionViewHolder =
        MovieItemCollectionViewHolder(AdapterMovieItemCollectionBinding.inflate(inflater, parent, false))

    inner class MovieItemCollectionViewHolder(private val binding: AdapterMovieItemCollectionBinding) : BaseViewHolder<Movie>(binding) {

        override fun bind(item: Movie, position: Int) {
            super.bind(item, position)

            binding.movieTitle.setResourceText(item.getMovieTitleFromAppLanguage(appLanguage))
            binding.image.loadImage(
                item.posterPath?.imageUrlFromTMDB(
                    ImageSize.PROFILE_185
                ),
                contentDescription = itemView.context.getString(R.string.movie_poster_description, item.title)
            )

            itemView.setOnClickListener {
                val id = item.id ?: return@setOnClickListener

                onMovieClicked?.invoke(id)
            }
        }
    }

}