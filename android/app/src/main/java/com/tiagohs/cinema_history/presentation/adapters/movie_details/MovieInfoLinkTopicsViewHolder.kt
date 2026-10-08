package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.R
import android.content.Intent
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoLinkTopicsBinding
import com.tiagohs.cinema_history.presentation.activities.MilMoviesPresentationActivity
import com.tiagohs.cinema_history.presentation.activities.PresentationActivity
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.MovieInfoType
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils

class MovieInfoLinkTopicsViewHolder(
    private val binding: AdapterMovieInfoLinkTopicsBinding,
    private val appLanguage: String,
    private val movieInfoType: MovieInfoType,
    private val onScreenLink: ((Intent) -> Unit)? = null
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val extraInfo = item.movie.extraInfo ?: return

        when (movieInfoType) {
            MovieInfoType.INFO_MIL_MOVIES -> bindMilMovies(extraInfo.milMoviesMainTopic, item.movie)
            MovieInfoType.INFO_HISTORY -> bindHistory(extraInfo.historyMainTopic, item.movie)
            else -> {
            }
        }
    }

    private fun bindMilMovies(milMoviesMainTopic: MilMoviesMainTopic?, movie: Movie) {
        val context = itemView.context
        val subtitleText = context.getString(
            R.string.movie_link_mil_movies_subtitle, movie.getMovieTitleFromAppLanguage(appLanguage) ?: ""
        )
        val titleText = context.getString(
            R.string.movie_link_mil_movies_title, milMoviesMainTopic?.title?.replaceFirstChar { it.titlecase() } ?: ""
        )
        val image = milMoviesMainTopic?.image
        val intent =
            MilMoviesPresentationActivity.newIntent(milMoviesMainTopic!!, itemView.context)

        bind(image, subtitleText, titleText, null, intent)
    }

    private fun bindHistory(mainTopicItem: MainTopicItem?, movie: Movie) {
        val context = itemView.context
        val subtitleText = context.getString(
            R.string.movie_link_history_subtitle, movie.getMovieTitleFromAppLanguage(appLanguage) ?: ""
        )
        val titleText = context.getString(
            R.string.movie_link_history_title, mainTopicItem?.title?.replaceFirstChar { it.titlecase() } ?: ""
        )
        val descriptionText = mainTopicItem?.description
        val image = mainTopicItem?.image
        val intent = PresentationActivity.newInstance(itemView.context, mainTopicItem!!)

        bind(image, subtitleText, titleText, descriptionText, intent)
    }

    private fun bind(
        image: Image?,
        subtitleText: String?,
        titleText: String?,
        descriptionText: String?,
        intent: Intent
    ) {
        val colorAsset = ColorUtils.getRandomColorAssets()
        val colorName = "md_${colorAsset.colorName}_500"

        binding.blockSpecialContainerCard.linkButtonContainerCard.setCardBackgroundColor(itemView.context.getResourceColor(colorName))
        binding.blockSpecialContainerCard.title.setResourceTextColor(colorName)

        image?.let { binding.blockSpecialContainerCard.mainTopicImage.loadImage(it, placeholder = null) }

        binding.blockSpecialContainerCard.subtitle.setResourceText(subtitleText)
        binding.blockSpecialContainerCard.subtitle.show()

        binding.blockSpecialContainerCard.title.setResourceText(titleText)
        binding.blockSpecialContainerCard.title.show()

        if (descriptionText != null) {
            binding.blockSpecialContainerCard.description.setResourceText(descriptionText)
            binding.blockSpecialContainerCard.description.show()
        } else {
            binding.blockSpecialContainerCard.description.hide()
        }

        binding.blockSpecialContainerCard.linkButtonContainer.setOnClickListener { onScreenLink?.invoke(intent) }
        binding.blockSpecialContainerCard.linkButtonContainerCard.setOnClickListener { onScreenLink?.invoke(intent) }
        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener { onScreenLink?.invoke(intent) }
    }
}