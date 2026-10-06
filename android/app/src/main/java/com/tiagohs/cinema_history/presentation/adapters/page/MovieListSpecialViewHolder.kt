package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageMovieListSpecialBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.extensions.setupParallaxScrollListener
import com.tiagohs.cinema_history.presentation.adapters.MovieItemSpecialAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentMovieListSpecial
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.DateUtils
import cz.intik.overflowindicator.SimpleSnapHelper

class MovieListSpecialViewHolder(
    private val binding: AdapterPageMovieListSpecialBinding,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BasePageViewHolder(binding) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val contentMovieList = item as? ContentMovieListSpecial ?: return
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

        binding.recyclerView.apply {
            adapter = personAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        setupContentFooterInformation(binding.footerContainer, contentMovieList.information)

        if (!isSetup) {
            binding.recyclerView.apply {
                binding.listIndicator.attachToRecyclerView(this)
                SimpleSnapHelper(binding.listIndicator).attachToRecyclerView(this)

                setupParallaxScrollListener()
            }
            isSetup = true
        }
    }
}