package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieWallpaperBinding
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage

class MovieWallpaperAdapter(
    list: List<Image>,
    val contentTitle: String?
) : BaseAdapter<Image, MovieWallpaperAdapter.MovieWallpaperViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieWallpaperViewHolder =
        MovieWallpaperViewHolder(AdapterMovieWallpaperBinding.inflate(inflater, parent, false))

    inner class MovieWallpaperViewHolder(private val binding: AdapterMovieWallpaperBinding) : BaseViewHolder<Image>(binding) {

        override fun bind(item: Image, position: Int) {
            super.bind(item, position)
            val context = itemView.context ?: return
            val imageUrl = item.filePath?.imageUrlFromTMDB(ImageSize.BACKDROP_300)

            binding.image.loadImage(imageUrl, itemView.context.getString(R.string.movie_backdrop_description, contentTitle), null, scaleType = "center_crop")
            binding.image.setOnClickListener {
                StfalconImageViewer.Builder<Image>(context, list) { view, image ->
                    val url = image.filePath?.imageUrlFromTMDB(ImageSize.BACKDROP_ORIGINAL)

                    view.loadImage(url, contentDescription = itemView.context.getString(R.string.movie_backdrop_description, contentTitle), placeholder = null, scaleType = null)
                }
                    .allowZooming(true)
                    .withTransitionFrom(binding.image)
                    .withStartPosition(position)
                    .show()
            }
        }
    }

}