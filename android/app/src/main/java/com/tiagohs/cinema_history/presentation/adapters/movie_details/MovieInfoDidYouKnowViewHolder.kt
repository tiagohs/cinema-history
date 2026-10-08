package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoDidYouKnowBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.presentation.adapters.DidYouKnowAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo

class MovieInfoDidYouKnowViewHolder(
    private val binding: AdapterMovieInfoDidYouKnowBinding
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val didYouKnow = item.movie.extraInfo?.didYouKnowList ?: emptyList()

        binding.didYouKnowList.apply {
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            adapter = DidYouKnowAdapter(didYouKnow)
        }
    }
}