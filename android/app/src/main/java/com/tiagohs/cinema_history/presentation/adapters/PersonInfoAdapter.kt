package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.person_details.*
import com.tiagohs.entities.enums.PersonInfoType
import com.tiagohs.entities.person_info.PersonInfo

class PersonInfoAdapter(
    list: List<PersonInfo>,
    private val isSpecial: Boolean
) : BaseAdapter<PersonInfo, BaseViewHolder<PersonInfo>>(list) {

    var onMovieSelected: ((movieId: Int) -> Unit)? = null
    var onVideoClick: ((String?) -> Unit)? = null
    var onLinkClick: ((String?) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        PersonInfoType.INFO_FILMOGRAPHY.ordinal -> PersonInfoFilmographyViewHolder.LAYOUT_ID
        PersonInfoType.INFO_BIOGRAPHY.ordinal -> PersonInfoBiographyViewHolder.LAYOUT_ID
        PersonInfoType.INFO_SPECIAL_BIOGRAPHY.ordinal -> PersonInfoSpecialBiographyViewHolder.LAYOUT_ID
        PersonInfoType.INFO_SPECIAL_FILMOGRAPHY.ordinal -> PersonInfoSpecialFilmographyViewHolder.LAYOUT_ID
        PersonInfoType.INFO_SPECIAL_PROFILE.ordinal -> PersonInfoSpecialProfileViewHolder.LAYOUT_ID
        PersonInfoType.INFO_MIDIA.ordinal -> PersonInfoMidiaViewHolder.LAYOUT_ID
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BaseViewHolder<PersonInfo> =
        when (viewType) {
            PersonInfoType.INFO_FILMOGRAPHY.ordinal -> PersonInfoFilmographyViewHolder(view, onMovieSelected)
            PersonInfoType.INFO_BIOGRAPHY.ordinal -> PersonInfoBiographyViewHolder(view)
            PersonInfoType.INFO_SPECIAL_BIOGRAPHY.ordinal -> PersonInfoSpecialBiographyViewHolder(view, onLinkClick)
            PersonInfoType.INFO_SPECIAL_FILMOGRAPHY.ordinal -> PersonInfoSpecialFilmographyViewHolder(view, onMovieSelected)
            PersonInfoType.INFO_SPECIAL_PROFILE.ordinal -> PersonInfoSpecialProfileViewHolder(view)
            PersonInfoType.INFO_MIDIA.ordinal -> PersonInfoMidiaViewHolder(view, onVideoClick, isSpecial)
            else -> object : BaseViewHolder<PersonInfo>(view) {}
        }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}