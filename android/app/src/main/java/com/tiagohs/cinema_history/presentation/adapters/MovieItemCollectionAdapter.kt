package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_movie_item_collection.*

class MovieItemCollectionAdapter(
    list: List<Movie>,
    val appLanguage: String,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null
) : BaseAdapter<Movie, MovieItemCollectionAdapter.MovieItemCollectionViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_item_collection

    override fun onCreateViewHolder(viewType: Int, view: View): MovieItemCollectionViewHolder =
        MovieItemCollectionViewHolder(view)

    inner class MovieItemCollectionViewHolder(view: View) : BaseViewHolder<Movie>(view) {

        override fun bind(item: Movie, position: Int) {
            super.bind(item, position)

            movieTitle.setResourceText(item.getMovieTitleFromAppLanguage(appLanguage))
            image.loadImage(
                item.posterPath?.imageUrlFromTMDB(
                    ImageSize.PROFILE_185
                ),
                contentDescription = containerView.context.getString(R.string.movie_poster_description, item.title)
            )

            containerView.setOnClickListener {
                val id = item.id ?: return@setOnClickListener

                onMovieClicked?.invoke(id)
            }
        }
    }

}