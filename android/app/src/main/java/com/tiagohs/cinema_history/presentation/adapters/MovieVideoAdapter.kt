package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieVideoBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.helpers.extensions.loadImage

class MovieVideoAdapter(
    list: List<Video>,
    val movie: Movie
) : BaseAdapter<Video, MovieVideoAdapter.MovieVideoViewHolder>(list) {

    var onVideoClick: ((String?) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieVideoViewHolder =
        MovieVideoViewHolder(AdapterMovieVideoBinding.inflate(inflater, parent, false))

    inner class MovieVideoViewHolder(private val binding: AdapterMovieVideoBinding) : BaseViewHolder<Video>(binding) {

        override fun bind(item: Video, position: Int) {
            super.bind(item, position)
            val context = itemView.context ?: return
            val videoId = item.key ?: return
            val videoThumbnailUrl = context.getString(R.string.youtube_image_link, videoId)

            binding.videoThumb.loadImage(videoThumbnailUrl, itemView.context.getString(R.string.movie_video_description, movie.originalTitle), null, scaleType = "center_crop")
            binding.videoContainer.setOnClickListener { onVideoClick?.invoke(videoId) }
        }
    }

}