package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.ColorAsset
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.contents.ContentBlockSpecial
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.view_block_special.*
import kotlinx.android.synthetic.main.view_line_five_colors.*

class MovieInfoBlockSpecialViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val contentBlockSpecial = item.movie.extraInfo?.blockSpecial ?: return
        val context = containerView.context ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()

        blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        blockSpecialDescription.setupLinkableTextView(context)

        bindMoviePoster(item.movie)
        bindTitle(contentBlockSpecial)
        bindCredits(contentBlockSpecial)
        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        blockSpecialClickHere.hide()
        blockSpecialContainer.setOnClickListener(null)
    }

    private fun bindMoviePoster(movie: Movie) {
        val posterPath = movie.posterPath

        if (posterPath != null) {
            val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return
            val imageStyle = ImageStyle(scaleType = "center_crop")
            val image = Image(ImageType.ONLINE, imageUrl, imageStyle = imageStyle, contentDescription = containerView.context.getString(R.string.movie_poster_description, movie.originalTitle))

            blockSpecialImage.show()
            blockSpecialImage.loadImage(image, null)
            return
        }

        blockSpecialImage.hide()
    }

    private fun bindTitle(contentBlockSpecial: ContentBlockSpecial) {
        val title = contentBlockSpecial.title

        if (title != null) {
            blockSpecialTitle.show()
            blockSpecialTitle.setResourceStyledText(title)
            return
        }

        blockSpecialTitle.hide()
    }

    private fun bindCredits(contentBlockSpecial: ContentBlockSpecial) {
        val credits = contentBlockSpecial.credits
        val context = containerView.context ?: return

        if (credits != null) {
            blockSpecialCredits.show()
            blockSpecialCredits.setResourceStyledText(credits)
            blockSpecialCredits.setupLinkableTextView(context)
            return
        }

        blockSpecialCredits.hide()
    }

    private fun bindClick(click: Click) {
        blockSpecialClickHere.show()
        blockSpecialClickHere.setResourceText(
            click.buttonText ?: containerView.context.getString(R.string.click_here_to_go)
        )
    }

    private fun bindColor(colorAsset: ColorAsset) {
        val context = containerView.context ?: return
        val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
        val linkColor = context.getResourceColor("md_${colorAsset.colorName}_900")

        blockSpecialContainerCard.setCardBackgroundColor(backgroundColor)
        blockSpecialTitle.setResourceTextColor(colorAsset.textColorName)
        blockSpecialDescription.setResourceTextColor(colorAsset.textColorName)
        blockSpecialDescription.setLinkTextColor(linkColor)
        blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)
        blockSpecialCredits.setResourceTextColor(colorAsset.textColorName)

        color1.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        color2.setResourceBackgroundColor("md_${colorAsset.colorName}_600")
        color3.setResourceBackgroundColor("md_${colorAsset.colorName}_700")
        color4.setResourceBackgroundColor("md_${colorAsset.colorName}_800")
        color5.setResourceBackgroundColor("md_${colorAsset.colorName}_900")
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_block_special
    }
}