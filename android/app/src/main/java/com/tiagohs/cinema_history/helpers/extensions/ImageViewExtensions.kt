package com.tiagohs.cinema_history.helpers.extensions

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.firebase.storage.FirebaseStorage
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageScaleType
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.image.ImageResize
import com.tiagohs.cinema_history.models.image.ImageStyle
import kotlinx.android.synthetic.main.activity_presentation.*

fun ImageView.setImageDrawableColored(drawableRes: Int, colorName: String) {
    val drawable = context.getResourceDrawable(drawableRes) ?: return
    val colorIdentifier = resources.getIdentifier(colorName, "color", context.packageName)

    setImageDrawableColored(drawable, colorIdentifier)
}

fun ImageView.setImageDrawableColored(drawableName: String, colorName: String) {
    val drawable = context.getResourceDrawable(drawableName) ?: return
    val colorIdentifier = resources.getIdentifier(colorName, "color", context.packageName)

    setImageDrawableColored(drawable, colorIdentifier)
}

fun ImageView.setImageDrawableColored(drawableRes: Int, color: Int) {
    val drawable = context.getResourceDrawable(drawableRes) ?: return

    setImageDrawableColored(drawable, color)
}

fun ImageView.setImageDrawableColored(drawable: Drawable, color: Int) {
    val wrappedDrawable = DrawableCompat.wrap(drawable)

    DrawableCompat.setTint(wrappedDrawable, context.getResourceColor(color))

    setImageDrawable(wrappedDrawable)
}

fun ImageView.loadImage(url: String?,
                        placeholder: Int? = R.drawable.placeholder_movie_poster,
                        errorPlaceholder: Int? = R.drawable.placeholder_movie_poster,
                        scaleType: String? = "center_inside",
                        oonLoadSuccess: (() -> Unit)? = null) {
    loadImage(Image(ImageType.ONLINE, url ?: "", imageStyle = ImageStyle(scaleType = scaleType)), placeholder, errorPlaceholder, oonLoadSuccess)
}

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    image: Image,
    placeholder: Int? = R.drawable.placeholder_movie_poster,
    errorPlaceholder: Int? = R.drawable.placeholder_movie_poster,
    onFinished: (() -> Unit)? = null) {

    val glide = Glide.with(context)
    val glideRequest = when (image.imageType) {
        ImageType.ONLINE -> glide.load(image.url)
        ImageType.LOCAL -> glide.load(context.getResourceDrawable(image.url))
        ImageType.ONLINE_FIREBASE -> {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl("gs://cinema-history.appspot.com")
            val imageRef = storageRef.child(image.url)

            glide.load(imageRef)
        }
    }

    placeholder?.let { glideRequest.placeholder(it) }
    errorPlaceholder?.let { glideRequest.error(it) }

    image.imageStyle?.resize?.let {
        val width = it.width?.convertIntToDp(context) ?: this.width
        val height = it.height?.convertIntToDp(context) ?: this.height

        glideRequest.override(width, height)
    }

    when (ImageScaleType.getImageViewScaleType(image.imageStyle?.scaleType)) {
        ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_CENTER,
        ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_END -> glideRequest.fitCenter()

        ImageView.ScaleType.CENTER_INSIDE -> glideRequest.centerInside()
        ImageView.ScaleType.CENTER_CROP -> glideRequest.centerCrop()

        else -> { glideRequest.centerCrop() }
    }

    glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL)
    glideRequest.transition(DrawableTransitionOptions.withCrossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
    glideRequest.listener(object : RequestListener<Drawable> {
        override fun onLoadFailed( e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            onFinished?.invoke()
            return false
        }
        override fun onResourceReady( resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onFinished?.invoke()
            return false
        }
    })

    glideRequest.into(this)
}