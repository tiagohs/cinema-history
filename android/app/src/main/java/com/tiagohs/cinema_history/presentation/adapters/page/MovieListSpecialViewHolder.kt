package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupParallaxScrollListener
import com.tiagohs.cinema_history.presentation.adapters.MovieItemSpecialAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentMovieListSpecial
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.DateUtils
import cz.intik.overflowindicator.SimpleSnapHelper
import kotlinx.android.synthetic.main.adapter_page_movie_list_special.*

class MovieListSpecialViewHolder(
    view: View,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BasePageViewHolder(view) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentMovieList = item as? ContentMovieListSpecial ?: return

        if (!isSetup) {
            val movies = contentMovieList.movies?.map {
                MovieFilmographyDTO(
                    it.id,
                    it.title ?: it.originalTitle,
                    it.posterPath,
                    it.backdropPath,
                    it.releaseDate,
                    it.overview,
                    year = if (it.releaseDate != null) DateUtils.getYearByDate(it.releaseDate) else null
                )
            } ?: emptyList()
            val personAdapter = MovieItemSpecialAdapter(movies).apply {
                onMovieClicked = this@MovieListSpecialViewHolder.onMovieSelected
            }

            recyclerView.apply {
                adapter = personAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                listIndicator.attachToRecyclerView(this)
                SimpleSnapHelper(listIndicator).attachToRecyclerView(this)

                setupParallaxScrollListener()
            }

            setupContentFooterInformation(contentMovieList.information)

            isSetup = true
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_movie_list_special
    }
}