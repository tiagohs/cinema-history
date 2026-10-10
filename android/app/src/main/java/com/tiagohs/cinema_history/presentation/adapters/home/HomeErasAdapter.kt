package com.tiagohs.cinema_history.presentation.adapters.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterHomeEraBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.show

/**
 * Carrossel M3 (CarouselLayoutManager) com as eras da História do Cinema.
 * Cada item usa a cor da era (campo "color" do maintopics.json) na etiqueta "Parte".
 */
class HomeErasAdapter(
    list: List<MainTopicItem>
) : BaseAdapter<MainTopicItem, HomeErasAdapter.EraViewHolder>(list) {

    var onEraClicked: ((MainTopicItem) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): EraViewHolder =
        EraViewHolder(AdapterHomeEraBinding.inflate(inflater, parent, false))

    inner class EraViewHolder(
        private val binding: AdapterHomeEraBinding
    ) : BaseViewHolder<MainTopicItem>(binding) {

        private val fadeThreshold = itemView.resources.getDimension(R.dimen.home_era_fade_threshold)

        init {
            // Itens comprimidos pelo carrossel escondem o texto aos poucos para não ficar cortado.
            binding.eraContainer.setOnMaskChangedListener { maskRect ->
                val fullWidth = binding.eraContainer.width.toFloat()
                val visibleWidth = maskRect.width()
                val alpha = if (fullWidth <= fadeThreshold) 1f
                else ((visibleWidth - fadeThreshold) / (fullWidth - fadeThreshold)).coerceIn(0f, 1f)

                binding.eraContent.alpha = alpha
                binding.eraBadge.alpha = alpha
            }
        }

        override fun bind(item: MainTopicItem, position: Int) {
            super.bind(item, position)
            val context = itemView.context

            binding.eraImage.loadImage(HomeImages.eraCover(item), placeholder = null)
            binding.eraTitle.text = item.title
            binding.eraDescription.text = item.description
            binding.eraPart.text = item.subtitle
            binding.eraPart.backgroundTintList = ColorStateList.valueOf(
                HomeImages.colorByName(context, item.color, ContextCompat.getColor(context, R.color.md_black_1000))
            )

            when {
                item.blocked -> {
                    binding.eraBadge.setText(R.string.comingsoon)
                    binding.eraBadge.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_red_500))
                    binding.eraBadge.show()
                }
                item.isNew -> {
                    binding.eraBadge.setText(R.string.is_new)
                    binding.eraBadge.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_green_500))
                    binding.eraBadge.show()
                }
                else -> binding.eraBadge.hide()
            }

            binding.eraContainer.contentDescription = "${item.subtitle}. ${item.title}"
            binding.eraContainer.isEnabled = !item.blocked
            binding.eraContainer.setOnClickListener {
                if (!item.blocked) onEraClicked?.invoke(item)
            }
        }
    }
}
