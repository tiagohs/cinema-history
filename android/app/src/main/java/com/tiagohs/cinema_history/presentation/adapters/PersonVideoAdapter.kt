package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.tmdb.person.PersonVideo
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_person_video.*

class PersonVideoAdapter(
    list: List<PersonVideo>,
    var onVideoClick: ((String?) -> Unit)? = null
) : BaseAdapter<PersonVideo, PersonVideoAdapter.PersonVideoViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_person_video

    override fun onCreateViewHolder(viewType: Int, view: View): PersonVideoViewHolder =
        PersonVideoViewHolder(view)

    inner class PersonVideoViewHolder(view: View) : BaseViewHolder<PersonVideo>(view) {

        override fun bind(item: PersonVideo, position: Int) {
            super.bind(item, position)
            val context = containerView.context ?: return
            val videoId = item.key

            videoThumb.loadImage(
                context.getString(R.string.youtube_image_link, videoId),
                null,
                scaleType = "center_crop"
            )
            videoContainer.setOnClickListener { onVideoClick?.invoke(videoId) }
            typeName.setResourceText(item.type)
            title.setResourceText(item.name)
            source.setResourceText(item.source)
        }
    }

}