package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageScaleType
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.image.Image
import kotlinx.android.synthetic.main.adapter_image.view.*

class ImageAdapter(
    val context: Context?,
    val images: List<Image>

): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_image, parent, false)

        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        holder.bind(image)
    }

    inner class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(image: Image) {
            itemView.image.loadImage(image)
        }
    }
}