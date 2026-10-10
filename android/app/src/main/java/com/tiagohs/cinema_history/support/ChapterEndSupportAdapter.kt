package com.tiagohs.cinema_history.support

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.databinding.ViewChapterSupportCardBinding

/**
 * Cartão discreto "apoie o app" no FIM do conteúdo de um capítulo (último item da lista), nunca entre
 * parágrafos. Também conta os capítulos concluídos (rolou até o fim) e só aparece a cada
 * [CHAPTERS_PER_CARD] capítulos concluídos, uma vez por marco. Nunca aparece para quem já apoia ou
 * fora da oferta (Brasil + português).
 *
 * Uso (HistoryPageFragment): `adapter = ChapterEndSupportAdapter.append(adapter, context, chave)`.
 */
class ChapterEndSupportAdapter private constructor(
    context: Context,
    private val chapterKey: String
) : RecyclerView.Adapter<ChapterEndSupportAdapter.CardViewHolder>() {

    private val appContext = context.applicationContext

    /** Marco (5, 10, 15… capítulos) que este capítulo pode mostrar; 0 = não mostra. */
    private val milestone = eligibleMilestone(appContext)
    private var visible = milestone > 0
    private var claimed = false

    private var recyclerView: RecyclerView? = null
    private var scrolledDown = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) scrolledDown = true
            if (scrolledDown && !rv.canScrollVertically(1)) {
                SupportStore.markChapterCompleted(appContext, chapterKey)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(scrollListener)
        this.recyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = if (visible) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(ViewChapterSupportCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Outro capítulo já mostrou este marco (ex.: duas páginas abertas) ou a pessoa apoiou: esconde.
        if (!claimed) {
            if (SupportStore.lastCardMilestone(appContext) >= milestone || !stillEligible()) {
                hide()
                return
            }
            claimed = true
            SupportStore.setLastCardMilestone(appContext, milestone)
        }

        holder.binding.chapterSupportOpen.setOnClickListener { view ->
            view.context.findActivity()?.let { Supporter.openSupportScreen(it, SOURCE) }
        }
        holder.binding.chapterSupportDismiss.setOnClickListener { hide() }
    }

    private fun stillEligible(): Boolean =
        Supporter.isOfferAvailable(appContext) && !Supporter.isSupporter(appContext)

    private fun hide() {
        if (!visible) return
        visible = false
        // Fora do layout/scroll em andamento.
        val rv = recyclerView
        if (rv != null) rv.post { notifyItemRemoved(0) } else notifyItemRemoved(0)
    }

    class CardViewHolder(val binding: ViewChapterSupportCardBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val CHAPTERS_PER_CARD = 5
        const val SOURCE = "chapter_end"

        /**
         * Devolve [content] com o cartão no fim (ConcatAdapter), ou o próprio [content] quando a oferta
         * não existe para este usuário (nem conta capítulos).
         */
        fun append(
            content: RecyclerView.Adapter<out RecyclerView.ViewHolder>?,
            context: Context?,
            chapterKey: String
        ): RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
            if (content == null || context == null) return content
            if (!Supporter.isOfferAvailable(context) || Supporter.isSupporter(context)) return content
            return ConcatAdapter(content, ChapterEndSupportAdapter(context, chapterKey))
        }

        private fun eligibleMilestone(context: Context): Int {
            val milestone = (SupportStore.completedChapters(context) / CHAPTERS_PER_CARD) * CHAPTERS_PER_CARD
            return if (milestone > 0 && milestone > SupportStore.lastCardMilestone(context)) milestone else 0
        }

        private tailrec fun Context.findActivity(): Activity? = when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.findActivity()
            else -> null
        }
    }
}
