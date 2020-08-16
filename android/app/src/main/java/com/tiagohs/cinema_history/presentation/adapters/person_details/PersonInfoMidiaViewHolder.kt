package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieWallpaperAdapter
import com.tiagohs.cinema_history.presentation.adapters.PersonVideoAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceTextColor
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_person_info_special_midia.view.*

class PersonInfoMidiaViewHolder(
    view: View,
    var onVideoClick: ((String?) -> Unit)? = null,
    private val isSpecial: Boolean
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val person = item.person

        bindImages(person)
        bindVideos(person)
    }

    private fun bindVideos(person: Person) {
        val context = containerView.context ?: return

        if (person.allImages.isNotEmpty()) {
            itemView.wallpapersList.show()

            itemView.wallpapersList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieWallpaperAdapter(person.allImages)
                addItemDecoration(
                    SpaceOffsetDecoration(
                        10.convertIntToDp(context),
                        SpaceOffsetDecoration.LEFT
                    )
                )
            }
        }

    }

    private fun bindImages(person: Person) {
        val context = containerView.context ?: return

        if (!person.extraInfo?.videos.isNullOrEmpty()) {
            val allVideos = person.extraInfo?.videos ?: emptyList()

            itemView.videoList.show()
            itemView.videoList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = PersonVideoAdapter(allVideos, onVideoClick)
                addItemDecoration(
                    SpaceOffsetDecoration(
                        10.convertIntToDp(context),
                        SpaceOffsetDecoration.LEFT
                    )
                )
            }
        }

        if (isSpecial) {
            itemView.personMidiaTitle.setResourceTextColor(R.color.md_grey_300)
            itemView.personMidiaContainer.setResourceBackgroundColor(R.color.md_black_1000)
            itemView.videoList.setResourceBackgroundColor(R.color.md_black_1000)
            itemView.wallpapersList.setResourceBackgroundColor(R.color.md_black_1000)
            return
        }

        itemView.personMidiaTitle.setResourceTextColor(R.color.md_black_1000)
        itemView.personMidiaContainer.setResourceBackgroundColor(R.color.md_white_1000)
        itemView.videoList.setResourceBackgroundColor(R.color.md_white_1000)
        itemView.wallpapersList.setResourceBackgroundColor(R.color.md_white_1000)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_midia
    }
}