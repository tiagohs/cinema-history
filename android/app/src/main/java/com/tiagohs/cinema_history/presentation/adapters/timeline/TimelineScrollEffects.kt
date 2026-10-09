package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.presentation.views.OdometerYearView

/**
 * Efeitos ligados ao scroll da linha do tempo:
 *
 * - a linha vertical se desenha até a "agulha" (um pouco abaixo do meio da tela);
 * - o acontecimento cujo marcador acabou de passar pela agulha acende (marcador cresce, ganha a
 *   cor da época e pulsa) e o ano grande da marca d'água troca como um odômetro;
 * - imagens com parallax suave; cada acontecimento entra com fade + slide na primeira vez.
 *
 * Performance: em onScrolled só percorre os filhos visíveis (getChildAt/getChildViewHolder, sem
 * alocar), e só altera propriedades de desenho (scaleY, translationY, alpha). Nada pede layout.
 * Com "Remover animações" ativo, a linha fica toda desenhada e nada se move.
 */
class TimelineScrollEffects(
    private val list: RecyclerView,
    private val watermark: OdometerYearView,
    private val motionEnabled: Boolean
) : RecyclerView.OnScrollListener(), RecyclerView.OnChildAttachStateChangeListener {

    private val revealed = SparseBooleanArray()
    private var activeHolder: TimelineItemViewHolder? = null
    private var activePosition = RecyclerView.NO_POSITION
    private var watermarkShown = false
    private var initialReveal = true
    private var initialIndex = 0

    private val updateRunnable = Runnable { update() }

    fun attach() {
        list.addOnScrollListener(this)
        list.addOnChildAttachStateChangeListener(this)
    }

    fun detach() {
        list.removeOnScrollListener(this)
        list.removeOnChildAttachStateChangeListener(this)
        list.removeCallbacks(updateRunnable)
        activeHolder = null
    }

    /** Conteúdo novo: tudo volta a "não revelado" e os efeitos são recalculados após o layout. */
    fun reset() {
        revealed.clear()
        activeHolder = null
        activePosition = RecyclerView.NO_POSITION
        initialReveal = true
        initialIndex = 0
        watermarkShown = false
        watermark.animate().cancel()
        watermark.alpha = 0f
        list.removeCallbacks(updateRunnable)
        list.post(updateRunnable)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy != 0) initialReveal = false
        update()
    }

    override fun onChildViewAttachedToWindow(view: View) {
        val holder = list.getChildViewHolder(view) as? TimelineItemViewHolder ?: return
        val position = holder.bindingAdapterPosition
        if (!motionEnabled || position == RecyclerView.NO_POSITION || revealed.get(position)) {
            holder.finishEntrance()
            return
        }
        revealed.put(position, true)
        // Na abertura da tela os primeiros itens entram em cascata; durante o scroll, na hora.
        val delay = if (initialReveal) (initialIndex++ * INITIAL_STAGGER_MS) else 0L
        holder.playEntrance(delay)
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        val holder = list.getChildViewHolder(view) as? TimelineItemViewHolder ?: return
        holder.finishEntrance()
        if (holder === activeHolder) {
            holder.setActive(false, animate = false)
            activeHolder = null
        }
    }

    fun update() {
        val height = list.height
        if (height == 0) return

        // Perto do fim da lista a agulha desce aos poucos, para os últimos anos também acenderem.
        val remaining = (list.computeVerticalScrollRange() - list.computeVerticalScrollOffset() -
            list.computeVerticalScrollExtent()).coerceAtLeast(0)
        val endZone = height * END_ZONE
        val endFraction = if (remaining >= endZone) 0f else 1f - remaining / endZone
        val needle = height * (NEEDLE + (NEEDLE_AT_END - NEEDLE) * endFraction)
        val center = height * 0.5f

        var newActive: TimelineItemViewHolder? = null
        for (i in 0 until list.childCount) {
            val child = list.getChildAt(i)
            val holder = list.getChildViewHolder(child)
            val top = child.top.toFloat()

            if (holder is TimelineLineHolder) {
                val progress = if (!motionEnabled || child.height == 0) 1f
                else ((needle - top) / child.height).coerceIn(0f, 1f)
                holder.setLineProgress(progress)
            }

            if (holder is TimelineItemViewHolder) {
                if (motionEnabled) {
                    holder.setParallax((top + child.height * 0.5f - center) / height)
                }
                if (top + holder.markerCenterY() <= needle) newActive = holder
            }
        }

        val newPosition = newActive?.bindingAdapterPosition ?: RecyclerView.NO_POSITION
        if (newActive !== activeHolder || newPosition != activePosition) {
            activeHolder?.takeIf { it !== newActive }?.setActive(false, animate = motionEnabled)
            newActive?.setActive(true, animate = motionEnabled)
            activeHolder = newActive
            activePosition = newPosition
            showYear(newActive?.yearValue ?: 0)
        }
    }

    private fun showYear(year: Int) {
        if (year <= 0) {
            if (watermarkShown) {
                watermarkShown = false
                fadeWatermark(0f)
            }
            return
        }
        watermark.setYear(year, animate = motionEnabled && watermarkShown)
        if (!watermarkShown) {
            watermarkShown = true
            fadeWatermark(WATERMARK_ALPHA)
        }
    }

    private fun fadeWatermark(alpha: Float) {
        watermark.animate().cancel()
        if (motionEnabled) {
            watermark.animate().alpha(alpha).setDuration(300).start()
        } else {
            watermark.alpha = alpha
        }
    }

    companion object {
        private const val NEEDLE = 0.58f
        private const val NEEDLE_AT_END = 0.92f
        private const val END_ZONE = 0.5f
        private const val INITIAL_STAGGER_MS = 90L
        const val WATERMARK_ALPHA = 0.9f
    }
}
