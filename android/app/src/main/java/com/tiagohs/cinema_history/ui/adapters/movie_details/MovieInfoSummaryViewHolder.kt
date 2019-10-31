package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.openLink
import com.tiagohs.cinema_history.helpers.extensions.toCurrency
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.helpers.utils.MovieUtils
import com.tiagohs.cinema_history.models.tmdb.ExternalIds
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import kotlinx.android.synthetic.main.adapter_movie_info_summary.view.*


class MovieInfoSummaryViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val overview = movie.overview ?: "Esse filme não possui uma sinopse disponível."

        itemView.movieSummary.text = overview
        itemView.movieSummary.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))

        setupExternalLinks(movie)
        setupSeekBarWithBudget(movie)
        setupRating(movie)
    }

    private fun setupExternalLinks(movie: Movie) {
        setupExternalLinkItem(movie.externalIds?.facebookId, itemView.facebookContainer, itemView.facebookContainerClickable, "https://www.facebook.com")
        setupExternalLinkItem(movie.externalIds?.twitterId, itemView.twitterContainer, itemView.twitterContainerClickable, "https://twitter.com")
        setupExternalLinkItem(movie.externalIds?.instagramId, itemView.instagramContainer, itemView.instagramContainerClickable, "https://instagram.com")
        setupExternalLinkItem(movie.externalIds?.imdbId, itemView.imdbContainer, itemView.imdbContainerClickable, "https://www.imdb.com/title")
        setupExternalLinkItem(movie.homepage, itemView.linkContainer, itemView.linkContainerClickable, "")
    }

    private fun setupExternalLinkItem(externalLinkId: String?, container: View, containerClickable: View, baseUrl: String) {
        val context = context ?: return
        val externalLinkID = externalLinkId ?: return

        if (externalLinkID.isNotBlank()) {
            val externalLink = if (baseUrl.isNotBlank()) "$baseUrl/$externalLinkID" else externalLinkID

            container.visibility = View.VISIBLE
            containerClickable.setOnClickListener { context.openLink(externalLink) }
        }

    }

    private fun setupSeekBarWithBudget(movie: Movie) {
        val budget = movie.budget
        val revenue = movie.revenue

        if (budget == null || revenue == null || budget == 0L || revenue == 0L) {
            itemView.budgetSeekBar.visibility = View.GONE
            itemView.budgetContainer.visibility = View.GONE
            return
        }

        itemView.budgetSeekBar.setOnTouchListener { _, _ -> false }
        itemView.budgetSeekBar.max = budget.toInt() + revenue.toInt()
        itemView.budgetSeekBar.progress = revenue.toInt()

        itemView.movieBudget.text = budget.toCurrency()
        itemView.movieRevenue.text = revenue.toCurrency()
    }

    private fun setupRating(movie: Movie) {
        val context = context ?: return
        val rating = MovieUtils.getRating(movie.releases?.countries) ?: return

        itemView.certificationCard.visibility = View.VISIBLE

        itemView.certificationCard.setCardBackgroundColor(context.getResourceColor(rating.backgroundColor))
        itemView.certification.setTextColor(context.getResourceColor(rating.textColor))
        itemView.certification.text = rating.rating
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_summary
    }
}