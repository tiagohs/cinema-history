package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.entities.tmdb.person.PersonVideo
import kotlinx.android.synthetic.main.adapter_person_video.view.*

class PersonVideoAdapter(
    val context: Context?,
    val videos: List<PersonVideo>
): RecyclerView.Adapter<PersonVideoAdapter.PersonVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonVideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_person_video, parent, false)

        return PersonVideoViewHolder(view)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: PersonVideoViewHolder, position: Int) {
        val video = videos[position]

        holder.bindVideo(video)
    }

    inner class PersonVideoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindVideo(video: PersonVideo) {
            val context = context ?: return
            val videoId = video.key
            val videoThumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"

            itemView.videoThumb.loadImage(videoThumbnailUrl, null, scaleType = "center_crop")
            itemView.videoContainer.setOnClickListener { context.openLink("https://www.youtube.com/watch?v=${videoId}") }

            itemView.typeName.text = video.type
            itemView.title.text = video.name
            itemView.source.text = video.source
        }
    }
}