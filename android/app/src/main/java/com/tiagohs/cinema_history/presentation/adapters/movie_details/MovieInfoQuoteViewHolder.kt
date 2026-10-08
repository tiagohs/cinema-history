package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoQuoteBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.setResourceImageColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils

class MovieInfoQuoteViewHolder(
    private val binding: AdapterMovieInfoQuoteBinding
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val contentQuote = item.movie.extraInfo?.quote ?: return

        binding.viewQuote.quoteText.setResourceText(contentQuote.quote.quote)
        binding.viewQuote.quoteTextAuthor.setResourceText(contentQuote.quote.author)

        val colorAsset = ColorUtils.getRandomColorAssets()
        val color = "md_${colorAsset.colorName}_500"

        binding.viewQuote.quoteTop.setResourceImageColor(color)
        binding.viewQuote.quoteBottom.setResourceImageColor(color)
    }
}