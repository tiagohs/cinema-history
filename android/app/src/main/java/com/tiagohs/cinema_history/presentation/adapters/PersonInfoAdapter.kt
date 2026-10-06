package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialProfileBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialMidiaBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialFilmographyBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialBiographyBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoBiographyBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoPersonListBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
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

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<PersonInfo> =
        when (viewType) {
            PersonInfoType.INFO_FILMOGRAPHY.ordinal -> PersonInfoFilmographyViewHolder(AdapterMovieInfoPersonListBinding.inflate(inflater, parent, false), onMovieSelected)
            PersonInfoType.INFO_BIOGRAPHY.ordinal -> PersonInfoBiographyViewHolder(AdapterPersonInfoBiographyBinding.inflate(inflater, parent, false))
            PersonInfoType.INFO_SPECIAL_BIOGRAPHY.ordinal -> PersonInfoSpecialBiographyViewHolder(AdapterPersonInfoSpecialBiographyBinding.inflate(inflater, parent, false), onLinkClick)
            PersonInfoType.INFO_SPECIAL_FILMOGRAPHY.ordinal -> PersonInfoSpecialFilmographyViewHolder(AdapterPersonInfoSpecialFilmographyBinding.inflate(inflater, parent, false), onMovieSelected)
            PersonInfoType.INFO_SPECIAL_PROFILE.ordinal -> PersonInfoSpecialProfileViewHolder(AdapterPersonInfoSpecialProfileBinding.inflate(inflater, parent, false))
            PersonInfoType.INFO_MIDIA.ordinal -> PersonInfoMidiaViewHolder(AdapterPersonInfoSpecialMidiaBinding.inflate(inflater, parent, false), onVideoClick, isSpecial)
            else -> object : BaseViewHolder<PersonInfo>(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
        }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}