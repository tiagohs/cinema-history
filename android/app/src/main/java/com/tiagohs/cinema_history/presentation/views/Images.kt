package com.tiagohs.cinema_history.presentation.views

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.utils.MediaUrls

/**
 * Carregamento de imagens das telas de prêmios e da linha do tempo, sempre no tamanho em que
 * serão exibidas (Glide decodifica em background já reduzido; nada é decodificado na main thread).
 */
object Images {

    private const val TMDB_BASE = "https://image.tmdb.org/t/p/"

    private val resourceIds = HashMap<String, Int>()

    fun tmdb(path: String?, size: ImageSize): String? {
        if (path.isNullOrBlank()) return null
        return TMDB_BASE + size.size + "/" + path.trimStart('/')
    }

    /** Modelo para o Glide: URL (online/Cloudflare) ou id do drawable (local), sem decodificar nada aqui. */
    fun model(context: Context, image: Image): Any? = when (image.imageType) {
        ImageType.LOCAL -> resourceId(context, image.url).takeIf { it != 0 }
        ImageType.ONLINE_FIREBASE -> MediaUrls.media(image.url)
        else -> image.url.takeIf { it.isNotBlank() }
    }

    private fun resourceId(context: Context, name: String): Int =
        resourceIds.getOrPut(name) { context.resources.getIdentifier(name, "drawable", context.packageName) }

    fun RequestBuilder<Drawable>.sized(widthPx: Int, heightPx: Int): RequestBuilder<Drawable> =
        if (widthPx > 0 && heightPx > 0) override(widthPx, heightPx) else this

    fun load(
        glide: RequestManager,
        target: ImageView,
        model: Any?,
        widthPx: Int,
        heightPx: Int,
        crossFade: Boolean,
        centerCrop: Boolean = true,
        listener: RequestListener<Drawable>? = null
    ) {
        if (model == null) {
            glide.clear(target)
            target.setImageDrawable(null)
            return
        }
        var request = glide.load(model)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .sized(widthPx, heightPx)
        request = if (centerCrop) request.centerCrop() else request.fitCenter()
        if (crossFade) request = request.transition(DrawableTransitionOptions.withCrossFade(180))
        request.listener(listener).into(target)
    }
}
