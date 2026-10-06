package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Intent
import com.tiagohs.cinema_history.databinding.AdapterPageLinkScreenBinding
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


class LinkScreenViewHolder(
    private val binding: AdapterPageLinkScreenBinding,
    private val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(binding) {

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

        binding.blockSpecialContainerCard.linkButtonContainerCard.setCardBackgroundColor(itemView.context.getResourceColor(colorName))
        binding.blockSpecialContainerCard.title.setResourceTextColor(colorName)

        image?.let { binding.blockSpecialContainerCard.mainTopicImage.loadImage(it, placeholder = null) }

        binding.blockSpecialContainerCard.subtitle.setResourceText(subtitleText)
        binding.blockSpecialContainerCard.subtitle.show()

        binding.blockSpecialContainerCard.title.setResourceText(titleText)
        binding.blockSpecialContainerCard.title.show()

        if (descriptionText != null) {
            binding.blockSpecialContainerCard.description.setResourceText(descriptionText)
            binding.blockSpecialContainerCard.description.show()
        } else {
            binding.blockSpecialContainerCard.description.hide()
        }

        binding.blockSpecialContainerCard.linkButtonContainer.setOnClickListener { onClickListener(click) }
        binding.blockSpecialContainerCard.linkButtonContainerCard.setOnClickListener { onClickListener(click) }
        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener { onClickListener(click) }
        binding.blockSpecialContainerCard.linkButtonText.setResourceText(
            click?.buttonText ?: itemView.context.getString(R.string.click_here_to_go)
        )

        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener { onClickListener(click) }
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

                itemView.context.openLink(link)
            }
            else -> {}
        }
    }
}
