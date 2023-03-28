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
import com.tiagohs.entities.contents.ContentLinkScreen
import com.tiagohs.entities.enums.Screen
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.view_link_topics.*


class LinkScreenViewHolder(
    val view: View,
    private val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentLinkScreen = item as? ContentLinkScreen ?: return

        bind(contentLinkScreen.image, contentLinkScreen.subtitle, contentLinkScreen.title, contentLinkScreen.description, contentLinkScreen.click)
    }

    private fun bind(
        image: Image?,
        subtitleText: String?,
        titleText: String?,
        descriptionText: String?,
        click: Click?
    ) {
        val colorAsset = ColorUtils.getRandomColorAssets()
        val colorName = "md_${colorAsset.colorName}_500"

        linkButtonContainerCard.setCardBackgroundColor(containerView.context.getResourceColor(colorName))
        title.setResourceTextColor(colorName)

        image?.let { mainTopicImage.loadImage(it, placeholder = null) }

        subtitle.setResourceText(subtitleText)
        subtitle.show()

        title.setResourceText(titleText)
        title.show()

        if (descriptionText != null) {
            description.setResourceText(descriptionText)
            description.show()
        } else {
            description.hide()
        }

        linkButtonContainer.setOnClickListener { onClickListener(click) }
        linkButtonContainerCard.setOnClickListener { onClickListener(click) }
        blockSpecialContainer.setOnClickListener { onClickListener(click) }
        linkButtonText.setResourceText(
            click?.buttonText ?: containerView.context.getString(R.string.click_here_to_go)
        )

        blockSpecialContainer.setOnClickListener { onClickListener(click) }
    }

    private fun onClickListener(click: Click?) {
        click ?: return

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

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_link_screen
    }
}
