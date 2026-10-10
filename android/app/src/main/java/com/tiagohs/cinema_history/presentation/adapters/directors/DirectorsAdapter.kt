package com.tiagohs.cinema_history.presentation.adapters.directors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterDirectorsCardBinding
import com.tiagohs.cinema_history.databinding.AdapterDirectorsEmptyBinding
import com.tiagohs.cinema_history.databinding.AdapterDirectorsIntroBinding
import com.tiagohs.cinema_history.databinding.AdapterDirectorsSectionBinding
import com.tiagohs.cinema_history.databinding.AdapterDirectorsTrendingBinding
import com.tiagohs.entities.main_topics.DirectorsMainTopic
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show

/**
 * Lista da tela "Diretores": introdução, carrossel "Em alta", cabeçalhos de época e cartões em grade.
 * Só os cartões ocupam uma coluna; o resto ocupa a linha toda ([isFullSpan]).
 */
class DirectorsAdapter(
    private val onDirectorSelected: (DirectorsMainTopic) -> Unit
) : ListAdapter<DirectorsRow, RecyclerView.ViewHolder>(DIFF) {

    /** Reaproveita as views do carrossel quando a lista é refeita (troca de filtro). */
    private val trendingPool = RecyclerView.RecycledViewPool()

    fun isFullSpan(position: Int): Boolean = getItemViewType(position) != TYPE_CARD

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DirectorsRow.Intro -> TYPE_INTRO
        is DirectorsRow.Section -> TYPE_SECTION
        is DirectorsRow.Trending -> TYPE_TRENDING
        is DirectorsRow.Card -> TYPE_CARD
        DirectorsRow.Empty -> TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INTRO -> IntroViewHolder(AdapterDirectorsIntroBinding.inflate(inflater, parent, false))
            TYPE_SECTION -> SectionViewHolder(AdapterDirectorsSectionBinding.inflate(inflater, parent, false))
            TYPE_TRENDING -> TrendingViewHolder(AdapterDirectorsTrendingBinding.inflate(inflater, parent, false))
            TYPE_CARD -> CardViewHolder(AdapterDirectorsCardBinding.inflate(inflater, parent, false))
            else -> EmptyViewHolder(AdapterDirectorsEmptyBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is DirectorsRow.Intro -> (holder as IntroViewHolder).bind(row)
            is DirectorsRow.Section -> (holder as SectionViewHolder).bind(row)
            is DirectorsRow.Trending -> (holder as TrendingViewHolder).bind(row)
            is DirectorsRow.Card -> (holder as CardViewHolder).bind(row)
            DirectorsRow.Empty -> Unit
        }
    }

    class IntroViewHolder(private val binding: AdapterDirectorsIntroBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: DirectorsRow.Intro) {
            binding.directorsIntro.text = itemView.context.getString(R.string.directors_intro, row.total)
        }
    }

    class SectionViewHolder(private val binding: AdapterDirectorsSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: DirectorsRow.Section) {
            val context = itemView.context
            val count = context.resources.getQuantityString(R.plurals.directors_count, row.count, row.count)
            val era = row.era

            if (era == null) {
                binding.sectionTitle.setText(R.string.directors_trending_title)
                binding.sectionSubtitle.text = context.getString(R.string.directors_trending_subtitle)
            } else {
                binding.sectionTitle.setText(era.title)
                binding.sectionSubtitle.text = "${context.getString(era.period)} · $count"
            }
        }
    }

    inner class TrendingViewHolder(private val binding: AdapterDirectorsTrendingBinding) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = DirectorsTrendingAdapter { onDirectorSelected(it) }

        init {
            binding.trendingCarousel.setRecycledViewPool(trendingPool)
            binding.trendingCarousel.adapter = adapter
        }

        fun bind(row: DirectorsRow.Trending) {
            adapter.submitList(row.directors)
        }
    }

    inner class CardViewHolder(private val binding: AdapterDirectorsCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: DirectorsRow.Card) {
            val context = itemView.context
            val director = row.director
            val meta = listOfNotNull(director.years, director.country).joinToString(" · ")

            DirectorImages.load(binding.directorImage, director.image)
            binding.directorName.text = director.title
            binding.directorMeta.text = meta

            when {
                row.showRank && director.trending > 0 -> {
                    binding.directorBadge.text = context.getString(R.string.directors_trending_rank, director.trending)
                    binding.directorBadge.show()
                }
                director.trending > 0 -> {
                    binding.directorBadge.setText(R.string.directors_trending_badge)
                    binding.directorBadge.show()
                }
                else -> binding.directorBadge.hide()
            }

            binding.directorCard.contentDescription = context.getString(
                R.string.directors_card_description, director.title, director.years.orEmpty(), director.country.orEmpty()
            )
            binding.directorCard.setOnClickListener { onDirectorSelected(director) }
        }
    }

    class EmptyViewHolder(binding: AdapterDirectorsEmptyBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_INTRO = 1
        const val TYPE_SECTION = 2
        const val TYPE_TRENDING = 3
        const val TYPE_CARD = 4
        const val TYPE_EMPTY = 5

        private val DIFF = object : DiffUtil.ItemCallback<DirectorsRow>() {
            override fun areItemsTheSame(oldItem: DirectorsRow, newItem: DirectorsRow) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DirectorsRow, newItem: DirectorsRow): Boolean = when {
                oldItem is DirectorsRow.Card && newItem is DirectorsRow.Card ->
                    oldItem.showRank == newItem.showRank && oldItem.director.personId == newItem.director.personId
                oldItem is DirectorsRow.Trending && newItem is DirectorsRow.Trending ->
                    oldItem.directors.map { it.personId } == newItem.directors.map { it.personId }
                else -> oldItem == newItem
            }
        }
    }
}
