package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import kotlinx.android.synthetic.main.adapter_movie_wallpaper.view.*

class MovieWallpaperAdapter(
    val context: Context?,
    val images: List<Image>
): RecyclerView.Adapter<MovieWallpaperAdapter.MovieWallpaperViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieWallpaperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_wallpaper, parent, false)

        return MovieWallpaperViewHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: MovieWallpaperViewHolder, position: Int) {
        val image = images[position]

        holder.bindImage(image)
    }

    inner class MovieWallpaperViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindImage(image: Image) {
            val context = context ?: return
            val imageUrl = image.filePath?.imageUrlFromTMDB(ImageSize.BACKDROP_300)

            itemView.image.loadImage(imageUrl, null, scaleType = "center_crop")
            itemView.image.setOnClickListener {
                StfalconImageViewer.Builder<Image>(context, images) { view, image ->
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