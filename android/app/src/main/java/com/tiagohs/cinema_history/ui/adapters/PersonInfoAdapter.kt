package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.person_info.PersonInfoMovieList
import com.tiagohs.cinema_history.ui.adapters.person_details.*
import com.tiagohs.entities.enums.PersonInfoType

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
            PersonInfoType.INFO_SPECIAL_BIOGRAPHY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoSpecialBiographyViewHolder.LAYOUT_ID, parent, false)

                PersonInfoSpecialBiographyViewHolder(context, view)
            }
            PersonInfoType.INFO_SPECIAL_FILMOGRAPHY.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoSpecialFilmographyViewHolder.LAYOUT_ID, parent, false)

                PersonInfoSpecialFilmographyViewHolder(context, view, onMovieSelected)
            }
            PersonInfoType.INFO_SPECIAL_PROFILE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoSpecialProfileViewHolder.LAYOUT_ID, parent, false)

                PersonInfoSpecialProfileViewHolder(context, view)
            }
            PersonInfoType.INFO_MIDIA.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(PersonInfoMidiaViewHolder.LAYOUT_ID, parent, false)

                PersonInfoMidiaViewHolder(context, view)
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
            PersonInfoType.INFO_SPECIAL_BIOGRAPHY.ordinal -> {
                val personInfo = list[position]
                val personInfoSpecialBiographyViewHolder = holder as? PersonInfoSpecialBiographyViewHolder ?: return

                personInfoSpecialBiographyViewHolder.bindPersonInfo(personInfo.person)
            }
            PersonInfoType.INFO_SPECIAL_FILMOGRAPHY.ordinal -> {
                val personInfo = list[position]
                val personInfoSpecialFilmographyViewHolder = holder as? PersonInfoSpecialFilmographyViewHolder ?: return

                personInfoSpecialFilmographyViewHolder.bindPersonInfo(personInfo.person)
            }
            PersonInfoType.INFO_SPECIAL_PROFILE.ordinal -> {
                val personInfo = list[position]
                val personInfoSpecialProfileViewHolder = holder as? PersonInfoSpecialProfileViewHolder ?: return

                personInfoSpecialProfileViewHolder.bindPersonInfo(personInfo.person)
            }
            PersonInfoType.INFO_MIDIA.ordinal -> {
                val personInfo = list[position]
                val personInfoMidiaViewHolder = holder as? PersonInfoMidiaViewHolder ?: return

                personInfoMidiaViewHolder.bindPersonInfo(personInfo.person)
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