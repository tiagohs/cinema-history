package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoReviewsBinding
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.ReviewAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.Review
import com.tiagohs.entities.tmdb.movie.ReviewsResult
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.utils.LocaleUtils

class MovieInfoReviewsViewHolder(
    private val binding: AdapterMovieInfoReviewsBinding,
    var onExtenalLink: ((String?) -> Unit)?,
    val appLanguage: String
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie

        setupSpinner(movie)
        movie.extraInfo?.reviewResults?.firstOrNull()?.let { setupReviewList(it) }
    }

    private fun setupSpinner(movie: Movie) {
        val reviewLanguages = movie.extraInfo?.reviewResults
                            ?.map { itemView.context.getResourceString(LocaleUtils.getCountryName(it.languageISO)) }

        if (!reviewLanguages.isNullOrEmpty()) {
            binding.languageSpinner.adapter = ArrayAdapter<String>(itemView.context, R.layout.support_simple_spinner_dropdown_item, reviewLanguages)
            binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val reviewResult = movie.extraInfo?.reviewResults?.getOrNull(position) ?: return

                    setupReviewList(reviewResult)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupReviewList(reviewResult: ReviewsResult) {
        val reviews = reviewResult.reviews

        if (!reviews.isNullOrEmpty()) {
            binding.reviewsList.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                adapter = ReviewAdapter(reviews, itemView.context.getResourceString(LocaleUtils.getCountryName(reviewResult.languageISO)), onExtenalLink)
            }
        }
    }
}