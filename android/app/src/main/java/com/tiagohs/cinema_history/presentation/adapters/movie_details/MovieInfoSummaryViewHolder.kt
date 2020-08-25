package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.MovieUtils
import kotlinx.android.synthetic.main.adapter_movie_info_summary.*


class MovieInfoSummaryViewHolder(
    view: View,
    private val appLanguage: String,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie

        setupMovieSummmary(movie)
        setupExternalLinks(movie)
        setupSeekBarWithBudget(movie)
        setupRating(movie)
    }

    private fun setupMovieSummmary(movie: Movie) {
        movieSummary.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))
        movieSummary.setResourceText(
            movie.getMovieSummaryFromAppLanguage(
                itemView.context.getResourceString(
                    R.string.no_summary
                ), appLanguage
            )
        )
    }

    private fun setupExternalLinks(movie: Movie) {
        setupExternalLinkItem(
            movie.externalIds?.facebookId,
            facebookContainer,
            facebookContainerClickable,
            R.string.facebook_link
        )
        setupExternalLinkItem(
            movie.externalIds?.twitterId,
            twitterContainer,
            twitterContainerClickable,
            R.string.twitter_link
        )
        setupExternalLinkItem(
            movie.externalIds?.instagramId,
            instagramContainer,
            instagramContainerClickable,
            R.string.instagram_link
        )
        setupExternalLinkItem(
            movie.externalIds?.imdbId,
            imdbContainer,
            imdbContainerClickable,
            R.string.imdb_link
        )
        setupExternalLinkItem(
            movie.homepage,
            linkContainer,
            linkContainerClickable,
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
            val externalLink =
                if (baseUrl == 0) externalLinkID else context.getString(baseUrl, externalLinkID)

            container.show()
            containerClickable.setOnClickListener { onExtenalLink?.invoke(externalLink) }
        }

    }

    private fun setupSeekBarWithBudget(movie: Movie) {
        val budget = movie.budget
        val revenue = movie.revenue

        if (budget == null || revenue == null || budget == 0L || revenue == 0L) {
            budgetSeekBar.hide()
            budgetContainer.hide()
            return
        }

        budgetSeekBar.setOnTouchListener { _, _ -> false }
        budgetSeekBar.max = budget.toInt() + revenue.toInt()
        budgetSeekBar.progress = revenue.toInt()

        movieBudget.setResourceText(budget.toCurrency())
        movieRevenue.setResourceText(revenue.toCurrency())
    }

    private fun setupRating(movie: Movie) {
        val context = containerView.context ?: return
        val rating = MovieUtils.getRating(movie.releases?.countries) ?: return

        certificationCard.show()

        certificationCard.setCardBackgroundColor(context.getResourceColor(rating.backgroundColor))
        certification.setResourceTextColor(rating.textColor)
        certification.setResourceText(rating.rating)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_summary
    }
}