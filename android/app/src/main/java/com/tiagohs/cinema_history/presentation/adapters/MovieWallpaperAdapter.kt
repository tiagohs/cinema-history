package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_movie_wallpaper.view.*

class MovieWallpaperAdapter(
    list: List<Image>
) : BaseAdapter<Image, MovieWallpaperAdapter.MovieWallpaperViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_wallpaper

    override fun onCreateViewHolder(viewType: Int, view: View): MovieWallpaperViewHolder =
        MovieWallpaperViewHolder(view)

    inner class MovieWallpaperViewHolder(view: View) : BaseViewHolder<Image>(view) {

        override fun bind(item: Image, position: Int) {
            super.bind(item, position)
            val context = containerView.context ?: return
            val imageUrl = item.filePath?.imageUrlFromTMDB(ImageSize.BACKDROP_300)

            itemView.image.loadImage(imageUrl, null, scaleType = "center_crop")
            itemView.image.setOnClickListener {
                StfalconImageViewer.Builder<Image>(context, list) { view, image ->
                    val url = image.filePath?.imageUrlFromTMDB(ImageSize.BACKDROP_ORIGINAL)
                    view.loadImage(url, null)
                }
                    .allowZooming(true)
                    .withTransitionFrom(itemView.image)
                    .show()
            }
        }
    }

}