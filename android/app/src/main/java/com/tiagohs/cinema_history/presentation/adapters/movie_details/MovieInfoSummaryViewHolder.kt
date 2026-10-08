package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoSummaryBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.MovieUtils


class MovieInfoSummaryViewHolder(
    private val binding: AdapterMovieInfoSummaryBinding,
    private val appLanguage: String,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie

        setupMovieSummmary(movie)
        setupExternalLinks(movie)
        setupSeekBarWithBudget(movie)
        setupRating(movie)
    }

    private fun setupMovieSummmary(movie: Movie) {
        binding.movieSummary.startAnimation(AnimationUtils.createFadeInAnimation(150, 200))
        binding.movieSummary.setResourceText(
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
            binding.facebookContainer,
            binding.facebookContainerClickable,
            R.string.facebook_link
        )
        setupExternalLinkItem(
            movie.externalIds?.twitterId,
            binding.twitterContainer,
            binding.twitterContainerClickable,
            R.string.twitter_link
        )
        setupExternalLinkItem(
            movie.externalIds?.instagramId,
            binding.instagramContainer,
            binding.instagramContainerClickable,
            R.string.instagram_link
        )
        setupExternalLinkItem(
            movie.externalIds?.imdbId,
            binding.imdbContainer,
            binding.imdbContainerClickable,
            R.string.imdb_link
        )
        setupExternalLinkItem(
            movie.homepage,
            binding.linkContainer,
            binding.linkContainerClickable,
            0
        )
    }

    private fun setupExternalLinkItem(
        externalLinkId: String?,
        container: View,
        containerClickable: View,
        baseUrl: Int
    ) {
        val context = itemView.context ?: return
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
            binding.budgetSeekBar.hide()
            binding.budgetContainer.hide()
            return
        }

        binding.budgetSeekBar.setOnTouchListener { _, _ -> false }
        binding.budgetSeekBar.max = budget.toInt() + revenue.toInt()
        binding.budgetSeekBar.progress = revenue.toInt()

        binding.movieBudget.setResourceText(budget.toCurrency())
        binding.movieRevenue.setResourceText(revenue.toCurrency())
    }

    private fun setupRating(movie: Movie) {
        val context = itemView.context ?: return
        val rating = MovieUtils.getRating(movie.releases?.countries) ?: return

        binding.certificationCard.show()

        binding.certificationCard.setCardBackgroundColor(context.getResourceColor(rating.backgroundColor))
        binding.certification.setResourceTextColor(rating.textColor)
        binding.certification.setResourceText(rating.rating)
    }
}