package com.tiagohs.cinema_history.presentation.configs

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.enums.ImageSize

/**
 * Adaptações para telas grandes (tablets, dobráveis, janelas redimensionáveis), seguindo as
 * window size classes do Material 3: compact < 600dp, medium 600–839dp, expanded ≥ 840dp.
 *
 * Tudo vem de recursos qualificados (values-sw600dp / values-sw600dp-w840dp, arquivo
 * dimens_large_screen.xml). No celular os valores são neutros, então nada muda. Ver docs/TABLETS.md.
 */
object LargeScreen {

    /** sw600dp ou mais (tablet, dobrável aberto). */
    fun isLarge(context: Context): Boolean = context.resources.getBoolean(R.bool.ls_is_large_screen)

    fun px(context: Context, @DimenRes id: Int): Int = context.resources.getDimensionPixelSize(id)

    fun integer(context: Context, @IntegerRes id: Int): Int = context.resources.getInteger(id)

    /** Fator para alturas fixas em dp que vêm do JSON (1.0 no celular). */
    fun mediaHeightScale(context: Context): Float =
        ResourcesCompat.getFloat(context.resources, R.dimen.ls_media_height_scale)

    /** Converte uma altura em dp (do JSON) para px, já escalada para a tela. */
    fun scaledHeightPx(context: Context, dp: Int): Int =
        (dp * context.resources.displayMetrics.density * mediaHeightScale(context)).toInt()

    /**
     * Tamanho de imagem do TMDB adequado à tela: em sw600dp as imagens são exibidas maiores,
     * então pede um degrau acima (ex.: w342 → w500, w780 → w1280). No celular devolve o mesmo.
     */
    fun tmdbSize(context: Context, size: ImageSize): ImageSize {
        if (!isLarge(context)) return size
        return when (size) {
            ImageSize.POSTER_92 -> ImageSize.POSTER_154
            ImageSize.POSTER_154 -> ImageSize.POSTER_342
            ImageSize.POSTER_185 -> ImageSize.POSTER_342
            ImageSize.POSTER_342 -> ImageSize.POSTER_500
            ImageSize.POSTER_500 -> ImageSize.POSTER_780
            ImageSize.BACKDROP_300 -> ImageSize.BACKDROP_780
            ImageSize.BACKDROP_780 -> ImageSize.BACKDROP_1280
            ImageSize.PROFILE_45 -> ImageSize.PROFILE_185
            ImageSize.PROFILE_185 -> ImageSize.PROFILE_632
            ImageSize.STIL_185 -> ImageSize.STIL_300
            ImageSize.LOGO_154 -> ImageSize.LOGO_300
            ImageSize.LOGO_300 -> ImageSize.LOGO_500
            else -> size
        }
    }
}

/** Atalho para [LargeScreen.tmdbSize]. */
fun ImageSize.forScreen(context: Context): ImageSize = LargeScreen.tmdbSize(context, this)

/**
 * Limita a largura útil do conteúdo de um container que ocupa a tela toda (RecyclerView,
 * NestedScrollView, ShimmerFrameLayout…), centralizando-o com padding horizontal simétrico.
 *
 * Diferente de limitar a largura da própria view, o container continua ocupando a tela inteira,
 * então a rolagem funciona também nas laterais (recomendação para listas em telas grandes).
 * Recalcula quando a janela muda de tamanho (multi-janela, dobrável). Com a dimensão = 0
 * (celular) não faz nada.
 */
fun View.limitContentWidth(@DimenRes maxWidthRes: Int) {
    val maxWidth = resources.getDimensionPixelSize(maxWidthRes)
    if (maxWidth <= 0 || getTag(R.id.ls_content_width_state) != null) return

    val baseStart = paddingStart
    val baseEnd = paddingEnd

    // Ajusta antes do desenho (sem "piscar" largo no primeiro frame). Confere a cada frame (só
    // compara inteiros) porque fitsSystemWindows/insets podem reescrever o padding depois.
    val preDraw = ViewTreeObserver.OnPreDrawListener {
        val w = width
        if (w <= 0) return@OnPreDrawListener true
        val extra = ((w - baseStart - baseEnd - maxWidth) / 2).coerceAtLeast(0)
        val start = baseStart + extra
        val end = baseEnd + extra
        if (paddingStart == start && paddingEnd == end) return@OnPreDrawListener true
        setPaddingRelative(start, paddingTop, end, paddingBottom)
        false
    }
    val attach = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            v.viewTreeObserver.addOnPreDrawListener(preDraw)
        }

        override fun onViewDetachedFromWindow(v: View) {
            v.viewTreeObserver.removeOnPreDrawListener(preDraw)
        }
    }
    setTag(R.id.ls_content_width_state, attach)
    addOnAttachStateChangeListener(attach)
    if (isAttachedToWindow) viewTreeObserver.addOnPreDrawListener(preDraw)
}

/**
 * Coluna de leitura dos capítulos: cada item da lista ganha margens laterais para não passar de
 * [textMaxWidth] (parágrafos, citações, ensaios) ou [mediaMaxWidth] (imagens, vídeos, carrosséis),
 * sempre centralizado. Como é uma ItemDecoration, os itens continuam com as margens internas de
 * sempre e a lista rola na tela inteira. Com larguras 0 (celular) não altera nada.
 */
class ReadingWidthDecoration(
    private val textMaxWidth: Int,
    private val mediaMaxWidth: Int,
    private val isMedia: (RecyclerView.ViewHolder) -> Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val holder = parent.getChildViewHolder(view) ?: return
        val max = if (isMedia(holder)) mediaMaxWidth else textMaxWidth
        if (max <= 0) return
        val available = parent.width - parent.paddingLeft - parent.paddingRight
        val extra = ((available - max) / 2).coerceAtLeast(0)
        outRect.left += extra
        outRect.right += extra
    }

    companion object {
        /** Instala a decoração e a recalcula quando a lista muda de largura (redimensionar janela). */
        fun install(list: RecyclerView, isMedia: (RecyclerView.ViewHolder) -> Boolean) {
            val context = list.context
            val text = LargeScreen.px(context, R.dimen.ls_reading_max_width)
            val media = LargeScreen.px(context, R.dimen.ls_media_max_width)
            if (text <= 0 && media <= 0) return
            list.addItemDecoration(ReadingWidthDecoration(text, media, isMedia))
            list.addOnLayoutChangeListener { v, left, _, right, _, oldLeft, _, oldRight, _ ->
                if (right - left != oldRight - oldLeft && oldRight - oldLeft > 0) {
                    v.post { (v as? RecyclerView)?.invalidateItemDecorations() }
                }
            }
        }
    }
}

/**
 * Grade "balanceada" para listas com itens de largura total no meio (citações, anúncios):
 * os itens comuns ocupam 1 coluna e, quando uma sequência não fecha a última linha, os que sobram
 * dividem a linha inteira (sem buracos). Usar com `GridLayoutManager(context, SPAN_UNITS)`.
 */
class BalancedGridSpanLookup(
    private val columns: Int,
    private val isFullSpan: (position: Int) -> Boolean,
    private val itemCount: () -> Int
) : GridLayoutManager.SpanSizeLookup() {

    init {
        isSpanIndexCacheEnabled = true
        isSpanGroupIndexCacheEnabled = true
    }

    override fun getSpanSize(position: Int): Int {
        if (columns <= 1 || isFullSpan(position)) return SPAN_UNITS
        var start = position
        while (start > 0 && !isFullSpan(start - 1)) start--
        var end = position
        val count = itemCount()
        while (end < count - 1 && !isFullSpan(end + 1)) end++
        val runLength = end - start + 1
        val index = position - start
        val remainder = runLength % columns
        return if (remainder != 0 && index >= runLength - remainder) SPAN_UNITS / remainder else SPAN_UNITS / columns
    }

    companion object {
        /** Divisível por 1, 2, 3, 4 e 6 colunas. */
        const val SPAN_UNITS = 12

        /**
         * Aplica a grade em [list] se a tela pede mais de uma coluna ([columnsRes]); no celular
         * devolve false e a lista continua como era.
         */
        fun applyIfMultiColumn(
            list: RecyclerView,
            @IntegerRes columnsRes: Int,
            isFullSpan: (position: Int) -> Boolean
        ): Boolean {
            val columns = list.context.resources.getInteger(columnsRes)
            if (columns <= 1) return false
            list.layoutManager = GridLayoutManager(list.context, SPAN_UNITS).apply {
                spanSizeLookup = BalancedGridSpanLookup(columns, isFullSpan) { list.adapter?.itemCount ?: 0 }
            }
            return true
        }

        /** Para listas com [ConcatAdapter] (anúncio nativo no meio): resolve o adapter/posição originais. */
        fun resolve(adapter: RecyclerView.Adapter<*>?, position: Int): Pair<RecyclerView.Adapter<*>, Int>? {
            if (adapter == null || position < 0 || position >= adapter.itemCount) return null
            if (adapter is ConcatAdapter) {
                val wrapped = adapter.getWrappedAdapterAndPosition(position)
                return wrapped.first to wrapped.second
            }
            return adapter to position
        }
    }
}
