package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.DidYouKnowAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import kotlinx.android.synthetic.main.adapter_movie_info_did_you_know.*

class MovieInfoDidYouKnowViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val didYouKnow = item.movie.extraInfo?.didYouKnowList ?: emptyList()

        didYouKnowList.apply {
            layoutManager =
                LinearLayoutManager(containerView.context, LinearLayoutManager.VERTICAL, false)
            adapter = DidYouKnowAdapter(didYouKnow)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_did_you_know
    }
}