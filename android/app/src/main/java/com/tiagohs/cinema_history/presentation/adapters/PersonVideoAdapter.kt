package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterPersonVideoBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.person.PersonVideo
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.setResourceText

class PersonVideoAdapter(
    list: List<PersonVideo>,
    var onVideoClick: ((String?) -> Unit)? = null
) : BaseAdapter<PersonVideo, PersonVideoAdapter.PersonVideoViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): PersonVideoViewHolder =
        PersonVideoViewHolder(AdapterPersonVideoBinding.inflate(inflater, parent, false))

    inner class PersonVideoViewHolder(private val binding: AdapterPersonVideoBinding) : BaseViewHolder<PersonVideo>(binding) {

        override fun bind(item: PersonVideo, position: Int) {
            super.bind(item, position)
            val context = itemView.context ?: return
            val videoId = item.key.trim()

            binding.videoThumb.loadImage(
                context.getString(R.string.youtube_image_link, videoId),
                null,
                scaleType = "center_crop"
            )
            binding.videoContainer.setOnClickListener { onVideoClick?.invoke(videoId) }
            binding.typeName.setResourceText(item.type)
            binding.title.setResourceText(item.name)
            binding.source.setResourceText(item.source)
        }
    }

}