package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Intent
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.activities.TimelineActivity
import com.tiagohs.entities.ColorAsset
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentBlockSpecial
import com.tiagohs.entities.enums.Screen
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_page_block_special.view.*
import kotlinx.android.synthetic.main.view_line_five_colors.view.*


class BlockSpecialViewHolder(
    val view: View,
    private val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentBlockSpecial = item as? ContentBlockSpecial ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()

        itemView.blockSpecialTitle.setResourceStyledText(contentBlockSpecial.title)
        itemView.blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        itemView.blockSpecialDescription.setupLinkableTextView(context)
        itemView.blockSpecialImage.loadImage(contentBlockSpecial.image, null)

        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        itemView.blockSpecialClickHere.hide()
        itemView.blockSpecialContainerCard.setOnClickListener(null)
    }

    private fun bindClick(click: Click) {
        itemView.blockSpecialClickHere.hide()
        itemView.blockSpecialContainerCard.setOnClickListener {
            when (click.screen) {
                Screen.TIMELINE_SCREEN -> {
                    val intent = TimelineActivity.newIntent(itemView.context).apply {
                        click.parameters?.forEach { parameter ->
                            putExtra(parameter.key, parameter.value)
                        }
                    }

                    presentScreen?.invoke(intent)
                }
            }
        }
    }

    private fun bindColor(colorAsset: ColorAsset) {
        val context = containerView.context ?: return
        val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")

        itemView.blockSpecialContainerCard.setCardBackgroundColor(backgroundColor)
        itemView.blockSpecialTitle.setResourceTextColor(colorAsset.textColorName)
        itemView.blockSpecialDescription.setResourceTextColor(colorAsset.textColorName)
        itemView.blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)

        itemView.color1.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        itemView.color2.setResourceBackgroundColor("md_${colorAsset.colorName}_600")
        itemView.color3.setResourceBackgroundColor("md_${colorAsset.colorName}_700")
        itemView.color4.setResourceBackgroundColor("md_${colorAsset.colorName}_800")
        itemView.color5.setResourceBackgroundColor("md_${colorAsset.colorName}_900")
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_block_special
    }
}
