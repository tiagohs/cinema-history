package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.setResourceImageColor
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.view_quote.*

class MovieInfoQuoteViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val contentQuote = item.movie.extraInfo?.quote ?: return

        quoteText.setResourceText(contentQuote.quote.quote)
        quoteTextAuthor.setResourceText(contentQuote.quote.author)

        val quoteMarkColor = contentQuote.quoteMarkColor
        if (quoteMarkColor != null) {
            quoteTop.setResourceImageColor(quoteMarkColor)
            quoteBottom.setResourceImageColor(quoteMarkColor)
            return
        }

        quoteTop.setResourceImageColor(R.color.md_white_1000)
        quoteBottom.setResourceImageColor(R.color.md_white_1000)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_quote
    }
}