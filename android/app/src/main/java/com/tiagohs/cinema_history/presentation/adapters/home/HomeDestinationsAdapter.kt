package com.tiagohs.cinema_history.presentation.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterHomeDestinationBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.helpers.extensions.loadImage

/**
 * Grade com as demais áreas do app (homecontent.json, exceto a História do Cinema, que fica no topo).
 * Títulos e subtítulos reaproveitam os textos da Home antiga.
 */
class HomeDestinationsAdapter(
    list: List<HomeContentItem>
) : BaseAdapter<HomeContentItem, HomeDestinationsAdapter.DestinationViewHolder>(list) {

    var onDestinationClicked: ((HomeContentItem) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): DestinationViewHolder =
        DestinationViewHolder(AdapterHomeDestinationBinding.inflate(inflater, parent, false))

    inner class DestinationViewHolder(
        private val binding: AdapterHomeDestinationBinding
    ) : BaseViewHolder<HomeContentItem>(binding) {

        override fun bind(item: HomeContentItem, position: Int) {
            super.bind(item, position)

            val (title, subtitle) = texts(item.mainTopicType)

            binding.destinationImage.loadImage(HomeImages.coverOf(item.image), placeholder = null)
            binding.destinationTitle.setText(title)
            binding.destinationSubtitle.setText(subtitle)
            binding.destinationCard.contentDescription = itemView.context.getString(title)
            binding.destinationCard.setOnClickListener { onDestinationClicked?.invoke(item) }
        }

        private fun texts(type: MainTopicsType): Pair<Int, Int> = when (type) {
            MainTopicsType.MIL_MOVIES -> R.string.mil_movies_title to R.string.home_milmovies_subtitle
            MainTopicsType.AWARDS -> R.string.home_awards_title to R.string.home_awards_subtitle
            MainTopicsType.TIMELINE -> R.string.timeline_title to R.string.home_timeline_description
            MainTopicsType.DIRECTORS -> R.string.home_directors_title to R.string.home_directors_description
            MainTopicsType.HISTORY_CINEMA -> R.string.history_cinema_title to R.string.start_journey
        }
    }
}
