package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Intent
import com.tiagohs.cinema_history.databinding.AdapterPageBlockSpecialBinding
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


class BlockSpecialViewHolder(
    private val binding: AdapterPageBlockSpecialBinding,
    private val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val contentBlockSpecial = item as? ContentBlockSpecial ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()

        binding.blockSpecialContainerCard.blockSpecialDescription.setResourceStyledText(contentBlockSpecial.description)
        binding.blockSpecialContainerCard.blockSpecialDescription.setupLinkableTextView(context)

        bindImage(contentBlockSpecial)
        bindTitle(contentBlockSpecial)
        bindCredits(contentBlockSpecial)
        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialClickHere.hide()
        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener(null)
    }

    private fun bindImage(contentBlockSpecial: ContentBlockSpecial) {
        val image = contentBlockSpecial.image

        if (image != null) {
            binding.blockSpecialContainerCard.blockSpecialImage.show()
            binding.blockSpecialContainerCard.blockSpecialImage.loadImage(image, null)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialImage.hide()
    }

    private fun bindTitle(contentBlockSpecial: ContentBlockSpecial) {
        val title = contentBlockSpecial.title

        if (title != null) {
            binding.blockSpecialContainerCard.blockSpecialTitle.show()
            binding.blockSpecialContainerCard.blockSpecialTitle.setResourceStyledText(title)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialTitle.hide()
    }


    private fun bindCredits(contentBlockSpecial: ContentBlockSpecial) {
        val credits = contentBlockSpecial.credits

        if (credits != null) {
            binding.blockSpecialContainerCard.blockSpecialCredits.show()
            binding.blockSpecialContainerCard.blockSpecialCredits.setResourceStyledText(credits)
            return
        }

        binding.blockSpecialContainerCard.blockSpecialCredits.hide()
    }

    private fun bindClick(click: Click) {
        binding.blockSpecialContainerCard.blockSpecialClickHere.show()
        binding.blockSpecialContainerCard.blockSpecialClickHere.setResourceText(
            click.buttonText ?: itemView.context.getString(R.string.click_here_to_go)
        )
        binding.blockSpecialContainerCard.blockSpecialContainer.setOnClickListener {
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

    private fun bindColor(colorAsset: ColorAsset) {
        val context = itemView.context ?: return
        val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
        val linkColor = context.getResourceColor("md_${colorAsset.colorName}_900")

        binding.blockSpecialContainerCard.root.setCardBackgroundColor(backgroundColor)
        binding.blockSpecialContainerCard.blockSpecialTitle.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialDescription.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialDescription.setLinkTextColor(linkColor)
        binding.blockSpecialContainerCard.blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialClickHere.setResourceTextColor(colorAsset.textColorName)
        binding.blockSpecialContainerCard.blockSpecialCredits.setResourceTextColor(colorAsset.textColorName)

        binding.blockSpecialContainerCard.viewLineFiveColors.color1.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
        binding.blockSpecialContainerCard.viewLineFiveColors.color2.setResourceBackgroundColor("md_${colorAsset.colorName}_600")
        binding.blockSpecialContainerCard.viewLineFiveColors.color3.setResourceBackgroundColor("md_${colorAsset.colorName}_700")
        binding.blockSpecialContainerCard.viewLineFiveColors.color4.setResourceBackgroundColor("md_${colorAsset.colorName}_800")
        binding.blockSpecialContainerCard.viewLineFiveColors.color5.setResourceBackgroundColor("md_${colorAsset.colorName}_900")
    }
}
