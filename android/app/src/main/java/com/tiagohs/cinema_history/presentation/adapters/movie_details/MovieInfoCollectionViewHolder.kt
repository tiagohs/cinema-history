package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoCollectionBinding
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieItemCollectionAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceTextColor
import com.tiagohs.helpers.tools.SliderTransformer
import com.tiagohs.helpers.utils.ColorUtils

class MovieInfoCollectionViewHolder(
    private val binding: AdapterMovieInfoCollectionBinding,
    val appLanguage: String,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null
) : BaseViewHolder<MovieInfo>(binding) {

    private var isSetup = false

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val context = itemView.context

        if (!isSetup) {
            val colorAsset = ColorUtils.getRandomColorAssets()
            val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
            val textColor = context.getResourceColor(colorAsset.textColorName)

            binding.collectionContainer.setBackgroundColor(context.getResourceColor(backgroundColor))
            binding.collectionTitle.setTextColor(textColor)
            binding.collectionTitle.setResourceText(context.getString(R.string.collection_title, movie.getMovieTitleFromAppLanguage(appLanguage)))
            binding.collectionViewPager.apply {
                adapter = MovieItemCollectionAdapter(movie.movieCollection?.movies ?: emptyList(), appLanguage, onMovieClicked)
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                offscreenPageLimit = 4

                setPageTransformer(SliderTransformer(4))
            }

            isSetup = true
        }
    }
}