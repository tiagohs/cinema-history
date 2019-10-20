package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.helpers.utils.LocaleUtils
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.models.tmdb.person.Person
import kotlinx.android.synthetic.main.adapter_movie_info_header.view.*
import kotlinx.android.synthetic.main.adapter_person_info_biography.view.*
import kotlin.math.abs

class PersonInfoBiographyViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {
        itemView.personBiography.text = person.biography
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_biography
    }
}