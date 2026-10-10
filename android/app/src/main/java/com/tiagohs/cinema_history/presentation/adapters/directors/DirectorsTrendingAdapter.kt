package com.tiagohs.cinema_history.presentation.adapters.directors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterDirectorsTrendingItemBinding
import com.tiagohs.entities.main_topics.DirectorsMainTopic

/** Itens do carrossel M3 "Em alta" (retrato grande, posição, nome, período/país e frase). */
class DirectorsTrendingAdapter(
    private val onDirectorSelected: (DirectorsMainTopic) -> Unit
) : ListAdapter<DirectorsMainTopic, DirectorsTrendingAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(AdapterDirectorsTrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterDirectorsTrendingItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val fadeThreshold = itemView.resources.getDimension(R.dimen.directors_trending_fade_threshold)

        init {
            // Itens comprimidos pelo carrossel escondem o texto aos poucos (igual às eras da Home).
            binding.trendingContainer.setOnMaskChangedListener { maskRect ->
                val fullWidth = binding.trendingContainer.width.toFloat()
                val alpha = if (fullWidth <= fadeThreshold) 1f
                else ((maskRect.width() - fadeThreshold) / (fullWidth - fadeThreshold)).coerceIn(0f, 1f)

                binding.trendingContent.alpha = alpha
                binding.trendingRank.alpha = alpha
            }
        }

        fun bind(director: DirectorsMainTopic) {
            val context = itemView.context

            DirectorImages.load(binding.trendingImage, director.image)
            binding.trendingRank.text = context.getString(R.string.directors_trending_rank, director.trending)
            binding.trendingName.text = director.title
            binding.trendingMeta.text = listOfNotNull(director.years, director.country).joinToString(" · ")
            binding.trendingTagline.text = director.description.orEmpty()

            binding.trendingContainer.contentDescription = context.getString(
                R.string.directors_card_description, director.title, director.years.orEmpty(), director.country.orEmpty()
            )
            binding.trendingContainer.setOnClickListener { onDirectorSelected(director) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DirectorsMainTopic>() {
            override fun areItemsTheSame(oldItem: DirectorsMainTopic, newItem: DirectorsMainTopic) =
                oldItem.personId == newItem.personId

            override fun areContentsTheSame(oldItem: DirectorsMainTopic, newItem: DirectorsMainTopic) =
                oldItem.personId == newItem.personId && oldItem.trending == newItem.trending
        }
    }
}
