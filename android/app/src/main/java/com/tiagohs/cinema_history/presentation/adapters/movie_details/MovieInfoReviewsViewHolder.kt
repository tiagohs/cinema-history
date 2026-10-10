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
import com.tiagohs.entities.tmdb.movie.ReviewsResult
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.utils.ContentLanguage

/**
 * "Críticas e reviews": as críticas ficam agrupadas pelo idioma em que foram escritas
 * (Português, Inglês, Espanhol). O idioma do app aparece primeiro no seletor.
 */
class MovieInfoReviewsViewHolder(
    private val binding: AdapterMovieInfoReviewsBinding,
    var onExtenalLink: ((String?) -> Unit)?,
    val appLanguage: String
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val results = orderedResults(item.movie.extraInfo?.reviewResults)

        setupSpinner(results)
        results.firstOrNull()?.let { setupReviewList(it) }
    }

    private fun orderedResults(results: List<ReviewsResult>?): List<ReviewsResult> {
        val current = ContentLanguage.current()
        val order = listOf(current, ContentLanguage.PORTUGUESE, ContentLanguage.ENGLISH, ContentLanguage.SPANISH).distinct()

        return results
            ?.filter { !it.reviews.isNullOrEmpty() }
            ?.sortedBy { order.indexOf(it.languageCode).let { index -> if (index < 0) order.size else index } }
            ?: emptyList()
    }

    private fun setupSpinner(results: List<ReviewsResult>) {
        val reviewLanguages = results.map { itemView.context.getResourceString(it.languageNameRes) }

        if (reviewLanguages.isNotEmpty()) {
            binding.languageSpinner.adapter = ArrayAdapter<String>(itemView.context, R.layout.support_simple_spinner_dropdown_item, reviewLanguages)
            binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val reviewResult = results.getOrNull(position) ?: return

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
                adapter = ReviewAdapter(reviews, itemView.context.getResourceString(reviewResult.languageNameRes), onExtenalLink)
            }
        }
    }
}
