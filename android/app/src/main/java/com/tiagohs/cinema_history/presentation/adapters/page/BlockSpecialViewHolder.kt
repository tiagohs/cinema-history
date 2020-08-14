package com.tiagohs.cinema_history.presentation.adapters.page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.activities.TimelineActivity
import com.tiagohs.entities.ColorAsset
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.click.ScreenParameter
import com.tiagohs.entities.contents.ContentBlockSpecial
import com.tiagohs.entities.enums.Screen
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.styledString
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_page_block_special.view.*
import kotlinx.android.synthetic.main.view_line_five_colors.view.*


class BlockSpecialViewHolder(
    val context: Context?,
    val view: View,
    val presentScreen: ((Intent) -> Unit)? = null
) : BasePageViewHolder(view) {

    fun bind(contentBlockSpecial: ContentBlockSpecial) {
        val context = context ?: return

        val colorAsset = ColorUtils.getRandomColorAssets()

        itemView.blockSpecialTitle.text = contentBlockSpecial.title

        itemView.blockSpecialDescription.text = contentBlockSpecial.description.styledString()
        itemView.blockSpecialDescription.setupLinkableTextView(context)

        itemView.blockSpecialImage.loadImage(contentBlockSpecial.image, null)

        bindColor(colorAsset)

        val click = contentBlockSpecial.click

        if (click != null) {
            bindClick(click)
            return
        }

        itemView.blockSpecialClickHere.visibility = View.GONE
        itemView.blockSpecialContainerCard.setOnClickListener(null)
    }

    private fun bindClick(click: Click) {
        itemView.blockSpecialClickHere.visibility = View.VISIBLE
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
        val context = context ?: return
        val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
        val textColor = context.getResourceColor(colorAsset.textColorName)

        itemView.blockSpecialContainerCard.setCardBackgroundColor(backgroundColor)

        itemView.blockSpecialTitle.setTextColor(textColor)
        itemView.blockSpecialDescription.setTextColor(textColor)
        itemView.blockSpecialClickHere.setTextColor(textColor)

        itemView.color1.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_500"))
        itemView.color2.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_600"))
        itemView.color3.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_700"))
        itemView.color4.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_800"))
        itemView.color5.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_block_special
    }
}
