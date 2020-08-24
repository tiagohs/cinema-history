package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_movie_item.*
import kotlinx.android.synthetic.main.adapter_movie_item.view.*

class MovieItemAdapter(
    list: List<MovieFilmographyDTO>
) : BaseAdapter<MovieFilmographyDTO, MovieItemAdapter.MovieItemViewHolder>(list) {

    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_item

    override fun onCreateViewHolder(viewType: Int, view: View): MovieItemViewHolder =
        MovieItemViewHolder(view)

    inner class MovieItemViewHolder(view: View) : BaseViewHolder<MovieFilmographyDTO>(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: MovieFilmographyDTO, position: Int) {
            super.bind(item, position)

            movieTitle.setResourceText(item.title)

            if (!(item.departments.isNullOrEmpty())) {
                movieDepartments.show()
                movieDepartments.setResourceText(containerView.context.getString(R.string.also_format, item.departments))
            }

            if (!(item.character.isNullOrEmpty())) {
                movieCharacter.show()
                movieCharacter.setResourceText(containerView.context.getString(R.string.as_format, item.character))
            }

            image.loadImage(
                item.posterPath?.imageUrlFromTMDB(
                    ImageSize.PROFILE_185
                ),
                contentDescription = containerView.context.getString(R.string.movie_poster_description, item.title)
            )
        }

        override fun onClick(v: View?) {
            val movieId = item?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }

}