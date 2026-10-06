package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageListMoviesBinding
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieItemCollectionAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentMovieList
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceTextColor
import com.tiagohs.helpers.tools.SliderTransformer
import com.tiagohs.helpers.utils.ColorUtils

class MovieListViewHolder(
    private val binding: AdapterPageListMoviesBinding,
    val mainTopic: MainTopicItem?,
    val appLanguage: String,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null
) : BasePageViewHolder(binding) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentMovieList = item as? ContentMovieList ?: return

        if (!isSetup) {
            val colorAsset = ColorUtils.getRandomColorAssets()

            binding.title.setResourceTextColor(colorAsset.textColorName)
            binding.title.setResourceText(R.string.should_see)
            binding.viewPager.apply {
                adapter = MovieItemCollectionAdapter(
                    contentMovieList.movies ?: emptyList(),
                    appLanguage,
                    onMovieClicked
                )
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                offscreenPageLimit = 4

                setPageTransformer(SliderTransformer(4))
            }

            bindMovieListBackground()

            isSetup = true
        }
    }

    private fun bindMovieListBackground() {
        val color = mainTopic?.color

        if (color != null) {
            binding.container.setResourceBackgroundColor(color)
            binding.title.setResourceTextColor(R.color.md_white_1000)
            return
        }

        val colorAsset = ColorUtils.getRandomColorAssets()

        binding.container.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        binding.title.setResourceTextColor(colorAsset.textColorName)
    }
}