package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MovieInfoType
import com.tiagohs.cinema_history.enums.PersonInfoType
import com.tiagohs.cinema_history.models.movie_info.PersonInfo
import com.tiagohs.cinema_history.models.movie_info.PersonInfoMovieList
import com.tiagohs.cinema_history.ui.adapters.movie_details.*
import com.tiagohs.cinema_history.ui.adapters.person_details.PersonInfoBiographyViewHolder
import com.tiagohs.cinema_history.ui.adapters.person_details.PersonInfoFilmographyViewHolder

class PersonInfoAdapter(
    val context: Context?,
    val list: List<PersonInfo>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onMovieSelected: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            PersonInfoType.INFO_FILMOGRAPHY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoFilmographyViewHolder.LAYOUT_ID, parent, false)

                PersonInfoFilmographyViewHolder(context, view, onMovieSelected)
            }
            PersonInfoType.INFO_BIOGRAPHY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoBiographyViewHolder.LAYOUT_ID, parent, false)

                PersonInfoBiographyViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoBiographyViewHolder.LAYOUT_ID, parent, false)

                PersonInfoBiographyViewHolder(context, view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            PersonInfoType.INFO_FILMOGRAPHY.ordinal -> {
                val personInfoMovieList = list[position] as? PersonInfoMovieList ?: return
                val movieInfoPersonViewHolder = holder as? PersonInfoFilmographyViewHolder ?: return

                movieInfoPersonViewHolder.bindPersonInfo(personInfoMovieList.listTitle, personInfoMovieList.movieList)
            }
            PersonInfoType.INFO_BIOGRAPHY.ordinal -> {
                val personInfo = list[position]
                val personInfoBiographyViewHolder = holder as? PersonInfoBiographyViewHolder ?: return

                personInfoBiographyViewHolder.bindPersonInfo(personInfo.person)
            }
            else -> {
                val personInfo = list[position]
                val personInfoBiographyViewHolder = holder as? PersonInfoBiographyViewHolder ?: return

                personInfoBiographyViewHolder.bindPersonInfo(personInfo.person)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }
}