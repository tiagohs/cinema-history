package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.MovieUtils
import kotlinx.android.synthetic.main.adapter_movie_info_summary.view.*


class MovieInfoSummaryViewHolder(
    view: View,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val overview =
            movie.overview ?: containerView.context.getResourceString(R.string.no_summary)

        itemView.movieSummary.setResourceText(overview)

        setupMovieSummmary(movie)
        setupExternalLinks(movie)
        setupSeekBarWithBudget(movie)
        setupRating(movie)
    }

    private fun setupMovieSummmary(movie: Movie) {
        val translations = movie.translations?.translations ?: emptyList()
        val originalLanguage = movie.originalLanguage

        itemView.movieSummary.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))

        val portugueseOverview =
            translations.find { it.iso_639_1 == "pt" && it.iso_3166_1 == "BR" }?.data?.overview
        if (!portugueseOverview.isNullOrBlank()) {
            itemView.movieSummary.setResourceText(portugueseOverview)
            return
        }

        val englishOverview =
            translations.find { it.iso_639_1 == "en" && it.iso_3166_1 == "US" }?.data?.overview
        if (!englishOverview.isNullOrBlank()) {
            itemView.movieSummary.setResourceText(englishOverview)
            return
        }

        val originalOverview =
            translations.find { it.iso_639_1 == originalLanguage }?.data?.overview
        if (!originalOverview.isNullOrBlank()) {
            itemView.movieSummary.setResourceText(originalOverview)
            return
        }

        itemView.movieSummary.setResourceText(R.string.no_summary)
    }

    private fun setupExternalLinks(movie: Movie) {
        setupExternalLinkItem(
            movie.externalIds?.facebookId,
            itemView.facebookContainer,
            itemView.facebookContainerClickable,
            R.string.facebook_link
        )
        setupExternalLinkItem(
            movie.externalIds?.twitterId,
            itemView.twitterContainer,
            itemView.twitterContainerClickable,
            R.string.twitter_link
        )
        setupExternalLinkItem(
            movie.externalIds?.instagramId,
            itemView.instagramContainer,
            itemView.instagramContainerClickable,
            R.string.instagram_link
        )
        setupExternalLinkItem(
            movie.externalIds?.imdbId,
            itemView.imdbContainer,
            itemView.imdbContainerClickable,
            R.string.imdb_link
        )
        setupExternalLinkItem(
            movie.homepage,
            itemView.linkContainer,
            itemView.linkContainerClickable,
            0
        )
    }

    private fun setupExternalLinkItem(
        externalLinkId: String?,
        container: View,
        containerClickable: View,
        baseUrl: Int
    ) {
        val context = containerView.context ?: return
        val externalLinkID = externalLinkId ?: return

        if (externalLinkID.isNotBlank()) {
            val externalLink = if (baseUrl == 0) externalLinkID else context.getString(baseUrl, externalLinkID)

            container.show()
            containerClickable.setOnClickListener { onExtenalLink?.invoke(externalLink)  }
        }

    }

    private fun setupSeekBarWithBudget(movie: Movie) {
        val budget = movie.budget
        val revenue = movie.revenue

        if (budget == null || revenue == null || budget == 0L || revenue == 0L) {
            itemView.budgetSeekBar.hide()
            itemView.budgetContainer.hide()
            return
        }

        itemView.budgetSeekBar.setOnTouchListener { _, _ -> false }
        itemView.budgetSeekBar.max = budget.toInt() + revenue.toInt()
        itemView.budgetSeekBar.progress = revenue.toInt()

        itemView.movieBudget.setResourceText(budget.toCurrency())
        itemView.movieRevenue.setResourceText(revenue.toCurrency())
    }

    private fun setupRating(movie: Movie) {
        val context = containerView.context ?: return
        val rating = MovieUtils.getRating(movie.releases?.countries) ?: return

        itemView.certificationCard.show()

        itemView.certificationCard.setCardBackgroundColor(context.getResourceColor(rating.backgroundColor))
        itemView.certification.setResourceTextColor(rating.textColor)
        itemView.certification.setResourceText(rating.rating)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_summary
    }
}