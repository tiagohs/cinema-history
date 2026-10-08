package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewRecomendationItemBinding
import com.tiagohs.cinema_history.databinding.AdapterPageRecomendationBinding
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentRecomendation
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils

class RecomendationsViewHolder(
    private val binding: AdapterPageRecomendationBinding
) : BasePageViewHolder(binding) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentRecomendation = item as? ContentRecomendation ?: return
        val context = itemView.context

        if (!isSetup) {
            val colorAsset = ColorUtils.getRandomColorAssets()

            contentRecomendation.list?.forEach { recomendation ->
                val itemBinding = ViewRecomendationItemBinding.inflate(LayoutInflater.from(context), null, false)
                itemBinding.root.layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.MATCH_PARENT,
                    Constraints.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 10.convertIntToDp(context))
                }

                itemBinding.container.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
                itemBinding.subtitle.setResourceTextColor(colorAsset.textColorName)
                itemBinding.title.setResourceTextColor(colorAsset.textColorName)
                itemBinding.linkButtonText.setResourceTextColor(colorAsset.textColorName)

                itemBinding.linkButtonContainerCard.setCardBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))

                recomendation.subtitle?.let {
                    itemBinding.subtitle.show()
                    itemBinding.subtitle.setResourceText(it)
                }
                recomendation.title?.let {
                    itemBinding.title.show()
                    itemBinding.title.setResourceText(it)
                }

                recomendation.description?.let {
                    itemBinding.description.show()
                    itemBinding.description.setResourceStyledText(it)
                    itemBinding.description.setupLinkableTextView(context)
                }

                itemBinding.container.setOnClickListener { context.openLink(recomendation.link) }
                itemBinding.containerCard.setOnClickListener { context.openLink(recomendation.link) }

                binding.recomendationContainer.addView(itemBinding.root)
            }

            isSetup = true
        }
    }
}