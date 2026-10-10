package com.tiagohs.cinema_history.audio.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.NativeAdAdapter
import com.tiagohs.cinema_history.audio.AudioAccess
import com.tiagohs.cinema_history.audio.AudioHost
import com.tiagohs.cinema_history.audio.AudioManifest
import com.tiagohs.cinema_history.audio.AudioRepository
import com.tiagohs.cinema_history.audio.ChapterAudioController
import com.tiagohs.cinema_history.audio.ChapterKey
import com.tiagohs.cinema_history.databinding.FragmentHistoryPageBinding
import com.tiagohs.cinema_history.presentation.adapters.page.QuoteViewHolder
import com.tiagohs.cinema_history.presentation.adapters.page.TextViewHolder
import io.reactivex.disposables.Disposable

/**
 * Liga a narração a uma página de capítulo (HistoryPageFragment):
 *  - botão "▶ Ouvir · N min" no cabeçalho (só se o manifest existir e o acesso permitir);
 *  - destaque suave do item do content_list sendo lido + rolagem acompanhando a leitura;
 *  - se o usuário rola por conta própria, o acompanhamento pausa e aparece o chip "Voltar ao trecho";
 *  - toque longo num parágrafo → "Ouvir a partir daqui";
 *  - esconde o mini-player quando o anúncio nativo da página passa perto dele.
 *
 * O content_list pode estar dividido em dois adapters com um anúncio no meio (ConcatAdapter):
 * [adAfter] e [adAdapter] convertem índice do content_list ↔ posição na RecyclerView.
 */
class PageAudioBinder(
    private val fragment: Fragment,
    private val binding: FragmentHistoryPageBinding,
    private val key: ChapterKey,
    private val adAfter: Int?,
    private val adAdapter: RecyclerView.Adapter<*>?
) : ChapterAudioController.Listener {

    private val rv = binding.pageContentList
    private val context = rv.context
    private val metrics: DisplayMetrics = context.resources.displayMetrics
    private fun dp(v: Int) = (v * metrics.density).toInt()

    private val host: AudioHost? get() = fragment.activity as? AudioHost

    private var manifest: AudioManifest? = null
    private var loadedFor: AudioAccess.Level? = null
    private var disposable: Disposable? = null
    private var attached = false

    private var highlighted: Int? = null
    private var following = true
    private var lastScrolledTo: Int? = null
    private var autoScrolling = false
    private var reservedBottom = 0

    val isAutoScrolling: Boolean get() = autoScrolling

    // --- ciclo de vida ------------------------------------------------------------------------

    fun attach() {
        val host = host ?: return
        attached = true
        rv.addItemDecoration(highlightDecoration, 0)
        rv.addItemDecoration(bottomSpaceDecoration)
        rv.addOnScrollListener(scrollListener)
        rv.addOnChildAttachStateChangeListener(childListener)
        binding.audioListenButton.setOnClickListener { onListenClicked() }
        binding.audioFollowChip.setOnClickListener { resumeFollowing() }
        host.audio.addListener(this)
        load(host.audio.access)
    }

    fun detach() {
        if (!attached) return
        attached = false
        disposable?.dispose()
        host?.audio?.removeListener(this)
        if (fragment.isResumed) host?.setMiniPlayerAdConflict(false)
        rv.removeItemDecoration(highlightDecoration)
        rv.removeItemDecoration(bottomSpaceDecoration)
        rv.removeOnScrollListener(scrollListener)
        rv.removeOnChildAttachStateChangeListener(childListener)
    }

    fun onResume() = checkAdConflict()

    /** A página deixou de ser a atual no ViewPager: ela não decide mais sobre o mini-player. */
    fun onPause() {
        host?.setMiniPlayerAdConflict(false)
    }

    private fun load(access: AudioAccess.Level) {
        loadedFor = access
        if (access == AudioAccess.Level.HIDDEN) {
            updateButton()
            return
        }
        disposable?.dispose()
        disposable = AudioRepository.manifest(context, key).subscribe(
            { m ->
                manifest = m
                host?.audio?.registerManifest(m)
                updateButton()
                refreshLongPress()
                onAudioChanged()
            },
            { updateButton() },
            { updateButton() }
        )
    }

    // --- botão do cabeçalho -------------------------------------------------------------------

    private fun updateButton() {
        val button = binding.audioListenButton
        val access = host?.audio?.access ?: AudioAccess.Level.HIDDEN
        val m = manifest
        if (access == AudioAccess.Level.HIDDEN || m == null) {
            button.isVisible = false
            return
        }
        val locked = access == AudioAccess.Level.LOCKED
        button.text = context.getString(R.string.audio_listen_button, m.minutes)
        button.setIconResource(if (locked) R.drawable.ic_audio_lock else R.drawable.ic_audio_play)
        button.contentDescription = context.getString(
            if (locked) R.string.audio_listen_button_locked_cd else R.string.audio_listen_button_cd, m.minutes
        )
        button.isVisible = true
    }

    private fun onListenClicked() {
        val host = host ?: return
        val m = manifest ?: return
        val audio = host.audio
        if (audio.currentKey() == key && audio.hasQueue()) {
            if (!audio.isActive()) audio.togglePlayPause()
            host.openAudioSheet()
            return
        }
        following = true
        lastScrolledTo = null
        audio.play(m)
    }

    // --- acompanhamento do texto --------------------------------------------------------------

    override fun onAudioChanged() {
        val host = host ?: return
        val audio = host.audio

        // o acesso mudou (ex.: compra concluída, ou flag de debug)
        if (loadedFor != audio.access) {
            if (manifest == null) load(audio.access) else loadedFor = audio.access
            updateButton()
            refreshLongPress()
        }

        val isThisChapter = audio.hasQueue() && audio.currentKey() == key
        val index = if (isThisChapter) audio.currentSourceIndex() else null
        if (index != highlighted) {
            highlighted = index
            rv.invalidate()
        }
        if (isThisChapter && following && audio.isActive() && index != null && index != lastScrolledTo) {
            lastScrolledTo = index
            scrollToContent(index)
        }
        binding.audioFollowChip.isVisible = isThisChapter && !following && audio.isActive() && index != null

        val reserve = host.miniPlayerReservedHeight()
        if (reserve != reservedBottom) {
            reservedBottom = reserve
            rv.invalidateItemDecorations()
        }
        checkAdConflict()
    }

    private fun resumeFollowing() {
        following = true
        lastScrolledTo = null
        binding.audioFollowChip.isVisible = false
        highlighted?.let {
            lastScrolledTo = it
            scrollToContent(it)
        }
    }

    private fun scrollToContent(contentIndex: Int) {
        val position = positionOf(contentIndex) ?: return
        val lm = rv.layoutManager as? LinearLayoutManager ?: return
        val topOffset = dp(72)
        val scroller = object : LinearSmoothScroller(context) {
            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int =
                boxStart + topOffset - viewStart

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
                super.calculateSpeedPerPixel(displayMetrics) * 2.5f
        }
        scroller.targetPosition = position
        autoScrolling = true
        lm.startSmoothScroll(scroller)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    autoScrolling = false
                    val audio = host?.audio ?: return
                    if (audio.hasQueue() && audio.currentKey() == key && audio.isActive() && following) {
                        // o usuário assumiu a rolagem: para de acompanhar até ele pedir de volta
                        following = false
                        onAudioChanged()
                    }
                }
                RecyclerView.SCROLL_STATE_IDLE -> autoScrolling = false
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            checkAdConflict()
        }
    }

    // --- índice do content_list ↔ posição na RecyclerView -------------------------------------

    private fun adCount(): Int = adAdapter?.itemCount ?: 0

    private fun positionOf(contentIndex: Int): Int? {
        if (contentIndex < 0) return null
        val after = adAfter ?: return contentIndex
        return if (contentIndex < after) contentIndex else contentIndex + adCount()
    }

    private fun contentIndexOf(position: Int): Int? {
        if (position == RecyclerView.NO_POSITION) return null
        val after = adAfter ?: return position
        val ads = adCount()
        return when {
            position < after -> position
            position < after + ads -> null
            else -> position - ads
        }
    }

    // --- destaque -----------------------------------------------------------------------------

    private val highlightDecoration = object : RecyclerView.ItemDecoration() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.audio_highlight)
        }
        private val rect = RectF()

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val index = highlighted ?: return
            val position = positionOf(index) ?: return
            val holder = parent.findViewHolderForAdapterPosition(position) ?: return
            val v = holder.itemView
            val padH = dp(8).toFloat()
            val padV = dp(6).toFloat()
            rect.set(
                v.left - padH, v.top + v.translationY - padV,
                v.right + padH, v.bottom + v.translationY + padV
            )
            val r = dp(12).toFloat()
            c.drawRoundRect(rect, r, r, paint)
        }
    }

    /** Espaço extra depois do último item para o mini-player não cobrir o fim do capítulo. */
    private val bottomSpaceDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (reservedBottom > 0 && position != RecyclerView.NO_POSITION && position == state.itemCount - 1) {
                outRect.bottom = reservedBottom
            }
        }
    }

    // --- "Ouvir a partir daqui" ---------------------------------------------------------------

    private val childListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) = setupLongPress(view)
        override fun onChildViewDetachedFromWindow(view: View) {}
    }

    private fun refreshLongPress() {
        for (i in 0 until rv.childCount) setupLongPress(rv.getChildAt(i))
    }

    private fun setupLongPress(view: View) {
        val holder = rv.getChildViewHolder(view) ?: return
        if (holder !is TextViewHolder && holder !is QuoteViewHolder) return
        val enabled = manifest != null && host?.audio?.access.let { it != null && it != AudioAccess.Level.HIDDEN }
        val targets = listOfNotNull<View>(view, view.findViewById<View>(R.id.contentText), view.findViewById<View>(R.id.quoteText))
        targets.forEach { t ->
            if (enabled) {
                t.setOnLongClickListener { anchor ->
                    val index = contentIndexOf(rv.getChildAdapterPosition(view))
                    val m = manifest
                    if (index == null || m == null || index !in m.narratedIndexes) return@setOnLongClickListener false
                    showListenFromHere(anchor, m, index)
                    true
                }
            } else {
                t.setOnLongClickListener(null)
                t.isLongClickable = false
            }
        }
    }

    private fun showListenFromHere(anchor: View, m: AudioManifest, index: Int) {
        val audio = host?.audio ?: return
        val locked = audio.access != AudioAccess.Level.UNLOCKED
        PopupMenu(context, anchor).apply {
            menu.add(
                Menu.NONE, 1, Menu.NONE,
                context.getString(if (locked) R.string.audio_listen_from_here_locked else R.string.audio_listen_from_here)
            )
            setOnMenuItemClickListener {
                following = true
                lastScrolledTo = index
                audio.playFromSource(m, index)
                true
            }
            show()
        }
    }

    // --- anúncio x mini-player ----------------------------------------------------------------

    /** Política de anúncios: nenhum controle de áudio sobre o anúncio nativo nem colado nele. */
    private fun checkAdConflict() {
        if (!fragment.isResumed) return
        val host = host ?: return
        val slot = host.miniPlayerSlot()
        if (slot == null) {
            host.setMiniPlayerAdConflict(false)
            return
        }
        val margin = dp(32)
        val loc = IntArray(2)
        var conflict = false
        for (i in 0 until rv.childCount) {
            val child = rv.getChildAt(i)
            if (rv.getChildViewHolder(child) !is NativeAdAdapter.NativeAdViewHolder) continue
            child.getLocationOnScreen(loc)
            val ad = Rect(loc[0], loc[1] - margin, loc[0] + child.width, loc[1] + child.height + margin)
            if (Rect.intersects(ad, slot)) {
                conflict = true
                break
            }
        }
        host.setMiniPlayerAdConflict(conflict)
    }
}
