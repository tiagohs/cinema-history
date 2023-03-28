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
import kotlinx.android.synthetic.main.view_block_special.*
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

        blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        blockSpecialDescription.setupLinkableTextView(context)

        bindImage(contentBlockSpecial)
        bindTitle(contentBlockSpecial)
        bindCredits(contentBlockSpecial)
        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        blockSpecialClickHere.hide()
        blockSpecialContainer.setOnClickListener(null)
    }

    private fun bindImage(contentBlockSpecial: ContentBlockSpecial) {
        val image = contentBlockSpecial.image

        if (image != null) {
            blockSpecialImage.show()
            blockSpecialImage.loadImage(image, null)
            return
        }

        blockSpecialImage.hide()
    }

    private fun bindTitle(contentBlockSpecial: ContentBlockSpecial) {
        val title = contentBlockSpecial.title

        if (title != null) {
            blockSpecialTitle.show()
            blockSpecialTitle.setResourceStyledText(title)
            return
        }

        blockSpecialTitle.hide()
    }


    private fun bindCredits(contentBlockSpecial: ContentBlockSpecial) {
        val credits = contentBlockSpecial.credits

        if (credits != null) {
            blockSpecialCredits.show()
            blockSpecialCredits.setResourceStyledText(credits)
            return
        }

        blockSpecialCredits.hide()
    }

    private fun bindClick(click: Click) {
        blockSpecialClickHere.show()
        blockSpecialClickHere.setResourceText(
            click.buttonText ?: containerView.context.getString(R.string.click_here_to_go)
        )
        blockSpecialContainer.setOnClickListener {
            when (click.screen) {
                Screen.TIMELINE_SCREEN -> {
                    val intent = TimelineActivity.newIntent(itemView.context).apply {
                        click.parameters?.forEach { parameter ->
                            putExtra(parameter.key, parameter.value)
                        }
                    }

                    presentScreen?.invoke(intent)
                }
                Screen.LINK_ONLINE -> {
                    val link = click.parameters?.firstOrNull()?.value

                    containerView.context.openLink(link)
                }
                else -> {}
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
        blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)
        blockSpecialCredits.setResourceTextColor(colorAsset.textColorName)

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
