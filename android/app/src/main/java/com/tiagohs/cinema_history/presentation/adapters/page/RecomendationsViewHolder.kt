package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentRecomendation
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_page_recomendation.*
import kotlinx.android.synthetic.main.view_recomendation_item.view.*

class RecomendationsViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentRecomendation = item as? ContentRecomendation ?: return
        val context = containerView.context

        if (!isSetup) {
            val colorAsset = ColorUtils.getRandomColorAssets()

            contentRecomendation.list?.forEach { recomendation ->
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.view_recomendation_item, null, false)
                        .apply {
                            layoutParams = Constraints.LayoutParams(
                                Constraints.LayoutParams.MATCH_PARENT,
                                Constraints.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(16.convertIntToDp(context), 0, 16.convertIntToDp(context), 10.convertIntToDp(context))
                            }
                        }

                view.container.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
                view.subtitle.setResourceTextColor(colorAsset.textColorName)
                view.title.setResourceTextColor(colorAsset.textColorName)
                view.linkButtonText.setResourceTextColor(colorAsset.textColorName)

                view.linkButtonContainerCard.setCardBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))

                recomendation.subtitle?.let {
                    view.subtitle.show()
                    view.subtitle.setResourceText(it)
                }
                recomendation.title?.let {
                    view.title.show()
                    view.title.setResourceText(it)
                }

                recomendation.description?.let {
                    view.description.show()
                    view.description.setResourceStyledText(it)
                    view.description.setupLinkableTextView(context)
                }

                view.container.setOnClickListener { context.openLink(recomendation.link) }
                view.containerCard.setOnClickListener { context.openLink(recomendation.link) }

                recomendationContainer.addView(view)
            }

            isSetup = true
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_recomendation
    }
}