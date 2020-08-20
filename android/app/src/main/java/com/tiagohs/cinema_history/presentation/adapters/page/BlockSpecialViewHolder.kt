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
import kotlinx.android.synthetic.main.adapter_page_block_special.*
import kotlinx.android.synthetic.main.view_line_five_colors.*


class BlockSpecialViewHolder(
    val view: View,
    private val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentBlockSpecial = item as? ContentBlockSpecial ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()

        blockSpecialTitle.setResourceStyledText(contentBlockSpecial.title)
        blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        blockSpecialDescription.setupLinkableTextView(context)
        blockSpecialImage.loadImage(contentBlockSpecial.image, null)

        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        blockSpecialClickHere.hide()
        blockSpecialContainerCard.setOnClickListener(null)
    }

    private fun bindClick(click: Click) {
        blockSpecialClickHere.show()
        blockSpecialContainerCard.setOnClickListener {
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
        val linkColor = context.getResourceColor("md_${colorAsset.colorName}_900")

        blockSpecialContainerCard.setCardBackgroundColor(backgroundColor)
        blockSpecialTitle.setResourceTextColor(colorAsset.textColorName)
        blockSpecialDescription.setResourceTextColor(colorAsset.textColorName)
        blockSpecialDescription.setLinkTextColor(linkColor)
        blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)

        color1.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        color2.setResourceBackgroundColor("md_${colorAsset.colorName}_600")
        color3.setResourceBackgroundColor("md_${colorAsset.colorName}_700")
        color4.setResourceBackgroundColor("md_${colorAsset.colorName}_800")
        color5.setResourceBackgroundColor("md_${colorAsset.colorName}_900")
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_block_special
    }
}
