package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterTimelineItemBinding
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.views.Images
import com.tiagohs.entities.enums.ImageScaleType
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineItem
import com.tiagohs.helpers.extensions.*
import kotlin.math.abs

/**
 * Um acontecimento da linha do tempo.
 *
 * Efeitos (controlados por TimelineScrollEffects; todos só com alpha/translation/scale):
 * - trecho da linha vertical que se desenha com o scroll;
 * - marcador que "acende" (cresce, ganha a cor da linha do tempo e pulsa) ao passar pelo centro;
 * - imagem com leve parallax e conteúdo que entra com fade + slide na primeira vez que aparece.
 */
class TimelineItemViewHolder(
    val color: String,
    val textColor: String,
    private val binding: AdapterTimelineItemBinding,
    private val glide: RequestManager,
    private val motionEnabled: Boolean
) : BaseViewHolder<Timeline>(binding), TimelineLineHolder {

    override val lineFill: View get() = binding.divisorFill
    override var lastLineProgress: Float = -1f

    /** Ano numérico (1895) para o odômetro; calculado no bind, nunca durante o scroll. */
    var yearValue: Int = 0
        private set

    private val context = itemView.context
    private val accentColor = context.getResourceColor(color)
    // Trilho/marcador: preto e branco no tema claro, invertidos no escuro.
    private val inkColor = context.getResourceColor(R.color.daynight_text_primary)
    private val whiteColor = context.getResourceColor(R.color.daynight_background)
    private val parallaxRange = context.resources.getDimension(R.dimen.timeline_parallax_range)
    private val enterOffset = context.resources.getDimension(R.dimen.timeline_enter_offset)

    private var active = false
    private var lastParallax = Float.NaN
    private var haloPulse: ObjectAnimator? = null

    init {
        bindColors()
        binding.divisorFill.pivotY = 0f
        binding.halo.backgroundTintList = ColorStateList.valueOf(accentColor)
        if (motionEnabled) {
            // Margem para o parallax não revelar as bordas da imagem.
            binding.image.scaleX = PARALLAX_SCALE
            binding.image.scaleY = PARALLAX_SCALE
        }
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timelineItem = item as? TimelineItem ?: return

        yearValue = parseYear(timelineItem.year)
        binding.year.setResourceText(timelineItem.year)
        binding.itemDescription.setResourceStyledText(timelineItem.description)
        binding.itemDescription.setupLinkableTextView(context)

        val title = timelineItem.title
        if (title != null) {
            binding.titleContainer.show()
            binding.title.setResourceStyledText(title)
            binding.title.setupLinkableTextView(context)
        } else {
            binding.titleContainer.hide()
        }

        // Só mexe na margem quando muda (evita novo LayoutParams a cada bind).
        val marginTop = (timelineItem.marginTop ?: DEFAULT_MARGIN_TOP).convertIntToDp(context)
        val params = binding.year.layoutParams as ViewGroup.MarginLayoutParams
        if (params.topMargin != marginTop) {
            params.topMargin = marginTop
            binding.year.layoutParams = params
        }

        bindImage(timelineItem)

        lastLineProgress = -1f
        lastParallax = Float.NaN
        setActive(false, animate = false)
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val image = timelineItem.image
        val model = image?.let { Images.model(context, it) }
        if (model == null) {
            glide.clear(binding.image)
            binding.image.setImageDrawable(null)
            binding.image.colorFilter = null
            return
        }

        val scaleType = ImageScaleType.getImageViewScaleType(image.imageStyle?.scaleType)
        val crop = scaleType == ImageView.ScaleType.CENTER_CROP
        binding.image.scaleType = if (crop) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
        // Mesma regra de antes: imagens "transparentes" coloridas, as demais em preto e branco.
        binding.image.colorFilter = if (timelineItem.imageTransparent) null else GRAYSCALE
        // Sem override: o Glide espera o tamanho da ImageView e decodifica já reduzido, fora da main thread.
        Images.load(glide, binding.image, model, 0, 0, crossFade = motionEnabled, centerCrop = crop)
    }

    fun markerCenterY(): Int = binding.circle.top + binding.circle.height / 2

    /** offset: posição do item em relação ao centro da lista (-1 em cima .. 1 embaixo). */
    fun setParallax(offset: Float) {
        val translation = -offset * parallaxRange
        if (!lastParallax.isNaN() && abs(translation - lastParallax) < 0.5f) return
        lastParallax = translation
        binding.image.translationY = translation
    }

    fun setActive(active: Boolean, animate: Boolean) {
        if (this.active == active && animate) return
        this.active = active

        binding.circleInner.setCardBackgroundColor(if (active) accentColor else whiteColor)
        binding.year.setTextColor(if (active) accentColor else inkColor)

        val scale = if (active) MARKER_ACTIVE_SCALE else 1f
        binding.circle.animate().cancel()
        if (animate && motionEnabled) {
            binding.circle.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(320)
                .setInterpolator(OVERSHOOT)
                .start()
        } else {
            binding.circle.scaleX = scale
            binding.circle.scaleY = scale
        }

        if (active && animate && motionEnabled) {
            pulse()
        } else {
            haloPulse?.cancel()
            binding.halo.alpha = 0f
        }
    }

    /** Três pulsos do halo (não fica animando para sempre: economiza bateria). */
    private fun pulse() {
        val animator = haloPulse ?: ObjectAnimator.ofPropertyValuesHolder(
            binding.halo,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 3.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 3.2f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.55f, 0f)
        ).apply {
            duration = 900
            repeatCount = 2
            interpolator = DECELERATE
        }.also { haloPulse = it }
        animator.cancel()
        animator.start()
    }

    /** Conteúdo entra com fade + slide (uma vez por item). */
    fun playEntrance(delay: Long) {
        if (!motionEnabled) return
        listOf(binding.containerTop, binding.containerBottom).forEachIndexed { index, view ->
            view.animate().cancel()
            view.alpha = 0f
            view.translationY = enterOffset * (1 + index * 0.5f)
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay + index * 70L)
                .setDuration(480)
                .setInterpolator(DECELERATE)
                .start()
        }
    }

    fun finishEntrance() {
        binding.containerTop.animate().cancel()
        binding.containerBottom.animate().cancel()
        binding.containerTop.alpha = 1f
        binding.containerTop.translationY = 0f
        binding.containerBottom.alpha = 1f
        binding.containerBottom.translationY = 0f
    }

    fun onRecycled() {
        finishEntrance()
        haloPulse?.cancel()
        binding.halo.alpha = 0f
        glide.clear(binding.image)
    }

    private fun bindColors() {
        val textColorRes = context.getResourceColor(textColor)

        binding.textLine.setCardBackgroundColor(textColorRes)
        binding.titleContainer.setResourceBackgroundColor(color)
        binding.itemDescription.setLinkTextColor(accentColor)
        binding.title.setTextColor(textColorRes)
    }

    companion object {
        private const val DEFAULT_MARGIN_TOP = 16
        private const val PARALLAX_SCALE = 1.14f
        private const val MARKER_ACTIVE_SCALE = 1.45f
        private val DECELERATE = DecelerateInterpolator(1.6f)
        private val OVERSHOOT = OvershootInterpolator(2.2f)
        private val GRAYSCALE = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

        /** "18\n95" -> 1895 (anos são sempre 4 dígitos, às vezes quebrados em duas linhas). */
        fun parseYear(raw: String?): Int {
            raw ?: return 0
            var value = 0
            var digits = 0
            for (c in raw) {
                if (c in '0'..'9') {
                    value = value * 10 + (c - '0')
                    digits++
                    if (digits == 4) break
                }
            }
            return if (digits == 4) value else 0
        }
    }
}
