package com.tiagohs.cinema_history.presentation.adapters.person_details

import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialMidiaBinding
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

class PersonInfoMidiaViewHolder(
    private val binding: AdapterPersonInfoSpecialMidiaBinding,
    var onVideoClick: ((String?) -> Unit)? = null,
    private val isSpecial: Boolean
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val person = item.person

        bindImages(person)
        bindVideos(person)
    }

    private fun bindVideos(person: Person) {
        val context = itemView.context ?: return

        if (person.allImages.isNotEmpty()) {
            binding.wallpapersList.show()

            binding.wallpapersList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieWallpaperAdapter(person.allImages, person.name)
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
        val context = itemView.context ?: return

        if (!person.extraInfo?.videos.isNullOrEmpty()) {
            val allVideos = person.extraInfo?.videos ?: emptyList()

            binding.videoList.show()
            binding.videoList.apply {
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
            binding.personMidiaTitle.setResourceTextColor(R.color.md_grey_300)
            binding.personMidiaContainer.setResourceBackgroundColor(R.color.md_black_1000)
            binding.videoList.setResourceBackgroundColor(R.color.md_black_1000)
            binding.wallpapersList.setResourceBackgroundColor(R.color.md_black_1000)
            return
        }

        // Página comum: segue o tema (claro/escuro).
        binding.personMidiaTitle.setResourceTextColor(R.color.daynight_text_primary)
        binding.personMidiaContainer.setResourceBackgroundColor(R.color.daynight_background)
        binding.videoList.setResourceBackgroundColor(R.color.daynight_background)
        binding.wallpapersList.setResourceBackgroundColor(R.color.daynight_background)
    }
}