package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieItemCollectionAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentMovieList
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.setResourceTextColor
import com.tiagohs.helpers.tools.SliderTransformer
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_page_list_movies.*

class MovieListViewHolder(
    view: View,
    val appLanguage: String,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null
) : BasePageViewHolder(view) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentMovieList = item as? ContentMovieList ?: return

        if (!isSetup) {
            val colorAsset = ColorUtils.getRandomColorAssets()

            container.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
            title.setResourceTextColor(colorAsset.textColorName)
            title.setResourceText(R.string.should_see)
            viewPager.apply {
                adapter = MovieItemCollectionAdapter(
                    contentMovieList.movies ?: emptyList(),
                    appLanguage,
                    onMovieClicked
                )
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                offscreenPageLimit = 4

                setPageTransformer(SliderTransformer(4))
            }

            isSetup = true
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_list_movies
    }
}