package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_movie_video.*

class MovieVideoAdapter(
    list: List<Video>
) : BaseAdapter<Video, MovieVideoAdapter.MovieVideoViewHolder>(list) {

    var onVideoClick: ((String?) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_video

    override fun onCreateViewHolder(viewType: Int, view: View): MovieVideoViewHolder =
        MovieVideoViewHolder(view)

    inner class MovieVideoViewHolder(view: View) : BaseViewHolder<Video>(view) {

        override fun bind(item: Video, position: Int) {
            super.bind(item, position)
            val context = containerView.context ?: return
            val videoId = item.key ?: return
            val videoThumbnailUrl = context.getString(R.string.youtube_image_link, videoId)

            videoThumb.loadImage(videoThumbnailUrl, null, scaleType = "center_crop")
            videoContainer.setOnClickListener { onVideoClick?.invoke(videoId) }
        }
    }

}