package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_image.view.*

class ImageAdapter(
    list: List<Image>
) : BaseAdapter<Image, ImageAdapter.ImageViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_image

    override fun onCreateViewHolder(viewType: Int, view: View): ImageViewHolder =
        ImageViewHolder(view)

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    class ImageViewHolder(view: View) : BaseViewHolder<Image>(view) {

        override fun bind(item: Image, position: Int) {
            super.bind(item, position)

            item.imageStyle?.height?.let {
                itemView.image.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(itemView.context)
                )
            }

            itemView.image.loadImage(item)
        }
    }
}