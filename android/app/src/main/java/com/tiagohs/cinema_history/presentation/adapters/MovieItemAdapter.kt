package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieItemBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.show

class MovieItemAdapter(
    list: List<MovieFilmographyDTO>
) : BaseAdapter<MovieFilmographyDTO, MovieItemAdapter.MovieItemViewHolder>(list) {

    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieItemViewHolder =
        MovieItemViewHolder(AdapterMovieItemBinding.inflate(inflater, parent, false))

    inner class MovieItemViewHolder(private val binding: AdapterMovieItemBinding) : BaseViewHolder<MovieFilmographyDTO>(binding),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: MovieFilmographyDTO, position: Int) {
            super.bind(item, position)

            binding.movieTitle.setResourceText(item.title)

            if (!(item.departments.isNullOrEmpty())) {
                binding.movieDepartments.show()
                binding.movieDepartments.setResourceText(itemView.context.getString(R.string.also_format, item.departments))
            }

            if (!(item.character.isNullOrEmpty())) {
                binding.movieCharacter.show()
                binding.movieCharacter.setResourceText(itemView.context.getString(R.string.as_format, item.character))
            }

            binding.image.loadImage(
                item.posterPath?.imageUrlFromTMDB(
                    ImageSize.PROFILE_185
                ),
                contentDescription = itemView.context.getString(R.string.movie_poster_description, item.title)
            )
        }

        override fun onClick(v: View?) {
            val movieId = item?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }

}