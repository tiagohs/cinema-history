package com.tiagohs.cinema_history.presentation.adapters

import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterImageBinding
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage

class ImageAdapter(
    list: List<Image>
) : BaseAdapter<Image, ImageAdapter.ImageViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): ImageViewHolder =
        ImageViewHolder(AdapterImageBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    class ImageViewHolder(private val binding: AdapterImageBinding) : BaseViewHolder<Image>(binding) {

        override fun bind(item: Image, position: Int) {
            super.bind(item, position)

            item.imageStyle?.height?.let {
                binding.image.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(itemView.context)
                )
            }

            binding.image.loadImage(item)
        }
    }
}