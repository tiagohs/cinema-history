package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.openLink
import com.tiagohs.cinema_history.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.cinema_history.models.tmdb.Image
import com.tiagohs.cinema_history.models.tmdb.movie.Video
import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.ui.adapters.MovieVideoAdapter
import com.tiagohs.cinema_history.ui.adapters.MovieWallpaperAdapter
import com.tiagohs.cinema_history.ui.adapters.PersonVideoAdapter
import kotlinx.android.synthetic.main.adapter_person_info_special_midia.view.*

class PersonInfoMidiaViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {
        bindImages(person)
        bindVideos(person)
    }

    private fun bindVideos(person: Person) {

        if (person.allImages.isNotEmpty()) {
            itemView.wallpapersList.visibility = View.VISIBLE

            itemView.wallpapersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemView.wallpapersList.adapter = MovieWallpaperAdapter(context, person.allImages)
            itemView.wallpapersList.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.LEFT))
        }

    }

    private fun bindImages(person: Person) {

        if (!person.extraInfo?.videos.isNullOrEmpty()) {
            itemView.videoList.visibility = View.VISIBLE

            val allVideos = person.extraInfo?.videos ?: emptyList()

            itemView.videoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemView.videoList.adapter = PersonVideoAdapter(context, allVideos)
            itemView.videoList.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.LEFT))
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_midia
    }
}