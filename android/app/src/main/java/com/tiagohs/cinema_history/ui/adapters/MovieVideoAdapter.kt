package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.extensions.openLink
import com.tiagohs.cinema_history.models.tmdb.movie.Video
import kotlinx.android.synthetic.main.adapter_movie_video.view.*

class MovieVideoAdapter(
    val context: Context?,
    val videos: List<Video>
): RecyclerView.Adapter<MovieVideoAdapter.MovieVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_video, parent, false)

        return MovieVideoViewHolder(view)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: MovieVideoViewHolder, position: Int) {
        val video = videos[position]

        holder.bindVideo(video)
    }

    inner class MovieVideoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindVideo(video: Video) {
            val context = context ?: return
            val videoId = video.key ?: return
            val videoThumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"

            itemView.videoThumb.loadImage(videoThumbnailUrl, null, scaleType = "center_crop")
            itemView.videoContainer.setOnClickListener { context.openLink("https://www.youtube.com/watch?v=${it}") }
        }
    }
}