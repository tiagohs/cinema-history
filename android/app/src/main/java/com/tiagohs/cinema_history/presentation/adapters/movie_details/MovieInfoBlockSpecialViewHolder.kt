package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoBlockSpecialBinding
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

class MovieInfoBlockSpecialViewHolder(
    private val binding: AdapterMovieInfoBlockSpecialBinding
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val contentBlockSpecial = item.movie.extraInfo?.blockSpecial ?: return
        val context = itemView.context ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()

        binding.blockSpecialContainerCard.blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        binding.blockSpecialContainerCard.blockSpecialDescription.setupLinkableTextView(context)

        bindMoviePoster(item.movie)
        bindTitle(contentBlockSpecial)
        bindCredits(contentBlockSpecial)
        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialClickHere.hide()
        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener(null)
    }

    private fun bindMoviePoster(movie: Movie) {
        val posterPath = movie.posterPath

        if (posterPath != null) {
            val imageUrl = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500) ?: return
            val imageStyle = ImageStyle(scaleType = "center_crop")
            val image = Image(ImageType.ONLINE, imageUrl, imageStyle = imageStyle, contentDescription = itemView.context.getString(R.string.movie_poster_description, movie.originalTitle))

            binding.blockSpecialContainerCard.blockSpecialImage.show()
            binding.blockSpecialContainerCard.blockSpecialImage.loadImage(image, null)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialImage.hide()
    }

    private fun bindTitle(contentBlockSpecial: ContentBlockSpecial) {
        val title = contentBlockSpecial.title

        if (title != null) {
            binding.blockSpecialContainerCard.blockSpecialTitle.show()
            binding.blockSpecialContainerCard.blockSpecialTitle.setResourceStyledText(title)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialTitle.hide()
    }

    private fun bindCredits(contentBlockSpecial: ContentBlockSpecial) {
        val credits = contentBlockSpecial.credits
        val context = itemView.context ?: return

        if (credits != null) {
            binding.blockSpecialContainerCard.blockSpecialCredits.show()
            binding.blockSpecialContainerCard.blockSpecialCredits.setResourceStyledText(credits)
            binding.blockSpecialContainerCard.blockSpecialCredits.setupLinkableTextView(context)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialCredits.hide()
    }

    private fun bindClick(click: Click) {
        binding.blockSpecialContainerCard.blockSpecialClickHere.show()
        binding.blockSpecialContainerCard.blockSpecialClickHere.setResourceText(
            click.buttonText ?: itemView.context.getString(R.string.click_here_to_go)
        )
    }

    private fun bindColor(colorAsset: ColorAsset) {
        val context = itemView.context ?: return
        val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
        val linkColor = context.getResourceColor("md_${colorAsset.colorName}_900")

        binding.blockSpecialContainerCard.root.setCardBackgroundColor(backgroundColor)
        binding.blockSpecialContainerCard.blockSpecialTitle.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialDescription.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialDescription.setLinkTextColor(linkColor)
        binding.blockSpecialContainerCard.blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialCredits.setResourceTextColor(colorAsset.textColorName)

        binding.blockSpecialContainerCard.viewLineFiveColors.color1.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        binding.blockSpecialContainerCard.viewLineFiveColors.color2.setResourceBackgroundColor("md_${colorAsset.colorName}_600")
        binding.blockSpecialContainerCard.viewLineFiveColors.color3.setResourceBackgroundColor("md_${colorAsset.colorName}_700")
        binding.blockSpecialContainerCard.viewLineFiveColors.color4.setResourceBackgroundColor("md_${colorAsset.colorName}_800")
        binding.blockSpecialContainerCard.viewLineFiveColors.color5.setResourceBackgroundColor("md_${colorAsset.colorName}_900")
    }
}