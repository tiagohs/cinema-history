package com.tiagohs.cinema_history.helpers.extensions

import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.tiagohs.cinema_history.enums.ImageScaleType
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.models.image.GifImage
import com.tiagohs.cinema_history.models.image.Image
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.*
import kotlinx.android.synthetic.main.adapter_page_gif.view.*
import kotlinx.android.synthetic.main.fragment_movie_presentation_item.*
import java.lang.Exception

fun ImageView.loadImage(url: String) {
    Picasso.get()
        .load(url)
        .into(this)
}

fun ImageView.loadImage(image: Image) {
    val picasso = Picasso.get()
    val picassoRequest: RequestCreator

    when (image.imageType) {
        ImageType.ONLINE -> {
            picassoRequest = picasso.load(image.url)
        }
        ImageType.LOCAL -> {
            val drawable = context.resources
                .getIdentifier(image.url, "drawable", context.packageName)

            picassoRequest = picasso.load(drawable)

            image.imageStyle?.scaleType?.let {
                when (ImageScaleType.getImageViewScaleType(it)) {
                    ImageView.ScaleType.CENTER_CROP -> {
                        picassoRequest.centerCrop()
                    }
                    ImageView.ScaleType.CENTER_INSIDE -> {
                        picassoRequest.centerInside()
                    }
                    ImageView.ScaleType.FIT_XY -> {
                        picassoRequest.fit()
                    }
                    else -> {}
                }
            }
        }
    }

    val screenWidth = context.resources.configuration.screenWidthDp

    image.imageStyle?.resize?.let {
        val imagewidth = it.width?.convertIntToDp(context) ?: screenWidth
        val imageHeight = it.height ?: 300

        picassoRequest.resize(imagewidth, imageHeight.convertIntToDp(context))
    }

    picassoRequest.into(this, object : Callback {
        override fun onSuccess() {
            image.imageStyle?.scaleType?.let {
                if (image.imageType == ImageType.ONLINE) {
                    scaleType = ImageScaleType.getImageViewScaleType(it)
                }

            }
        }

        override fun onError(e: Exception?) {

        }
    })
}

fun ImageView.loadGif(gifImage: GifImage) {
    val glide = Glide.with(context)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    val newLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    newLayoutParams.width = gifImage.imageStyle?.width ?: LinearLayout.LayoutParams.MATCH_PARENT
    newLayoutParams.height = gifImage.imageStyle?.height ?: 210

    layoutParams = newLayoutParams

    when (gifImage.imageType) {
        ImageType.LOCAL -> {
            val gifFile = context.resources
                .getIdentifier(gifImage.url, "raw", context.packageName)

            glide.load(gifFile).into(this)
        }
        ImageType.ONLINE -> {
            glide.load(gifImage.url).into(this)
        }
    }

    gifImage.imageStyle?.scaleType?.let {
        scaleType = ImageScaleType.getImageViewScaleType(it)
    }

}