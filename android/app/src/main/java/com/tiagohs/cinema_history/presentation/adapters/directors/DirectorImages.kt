package com.tiagohs.cinema_history.presentation.adapters.directors

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.loadImage

/**
 * Retratos da lista de diretores. As fotos ficam em drawable (assets do app); o Glide decodifica
 * fora da thread principal e guarda em cache, o que deixa a rolagem da grade leve.
 */
object DirectorImages {

    private val ids = HashMap<String, Int>()

    fun load(view: ImageView, image: Image?) {
        val name = image?.url?.takeIf { it.isNotBlank() } ?: return
        if (image.imageType != ImageType.LOCAL) {
            view.loadImage(image, placeholder = null)
            return
        }

        val id = resId(view.context, name)
        if (id == 0) {
            view.setImageDrawable(null)
            return
        }

        Glide.with(view)
            .load(id)
            .centerCrop()
            .into(view)
    }

    @SuppressLint("DiscouragedApi")
    private fun resId(context: Context, name: String): Int =
        ids.getOrPut(name) { context.resources.getIdentifier(name, "drawable", context.packageName) }
}
