package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.content.Intent
import android.view.View
import com.tiagohs.cinema_history.R
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
import kotlinx.android.synthetic.main.view_link_topics.*

class MovieInfoLinkTopicsViewHolder(
    view: View,
    private val appLanguage: String,
    private val movieInfoType: MovieInfoType,
    private val onScreenLink: ((Intent) -> Unit)? = null
) : BaseViewHolder<MovieInfo>(view) {

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
        val subtitleText =
            "O filme ${movie.getMovieTitleFromAppLanguage(appLanguage)} faz parte da lista"
        val titleText =
            "1001 Filmes para ver antes de morrer ${milMoviesMainTopic?.title?.capitalize()}"
        val image = milMoviesMainTopic?.image
        val intent =
            MilMoviesPresentationActivity.newIntent(milMoviesMainTopic!!, containerView.context)

        bind(image, subtitleText, titleText, null, intent)
    }

    private fun bindHistory(mainTopicItem: MainTopicItem?, movie: Movie) {
        val subtitleText =
            "Leia mais sobre o período em que ${movie.getMovieTitleFromAppLanguage(appLanguage)} foi lançado"
        val titleText = "A História do cinema ${mainTopicItem?.title?.capitalize()}"
        val descriptionText = mainTopicItem?.description
        val image = mainTopicItem?.image
        val intent = PresentationActivity.newInstance(containerView.context, mainTopicItem!!)

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

        linkButtonContainerCard.setCardBackgroundColor(containerView.context.getResourceColor(colorName))
        title.setResourceTextColor(colorName)

        image?.let { mainTopicImage.loadImage(it, placeholder = null) }

        subtitle.setResourceText(subtitleText)
        subtitle.show()

        title.setResourceText(titleText)
        title.show()

        if (descriptionText != null) {
            description.setResourceText(descriptionText)
            description.show()
        } else {
            description.hide()
        }

        linkButtonContainer.setOnClickListener { onScreenLink?.invoke(intent) }
        linkButtonContainerCard.setOnClickListener { onScreenLink?.invoke(intent) }
        blockSpecialContainer.setOnClickListener { onScreenLink?.invoke(intent) }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_link_topics
    }
}