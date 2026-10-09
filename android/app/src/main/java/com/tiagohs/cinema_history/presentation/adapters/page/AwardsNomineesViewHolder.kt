package com.tiagohs.cinema_history.presentation.adapters.page

import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterAwardCategoryRowBinding
import com.tiagohs.cinema_history.presentation.adapters.NomineeAdapter
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItem

/**
 * Uma categoria do ano: título + fileira horizontal de pôsteres.
 *
 * Performance: todas as fileiras compartilham o mesmo RecycledViewPool (os cartões criados numa
 * categoria são reaproveitados nas outras), a lista tem altura fixa (setHasFixedSize) e o
 * LayoutManager pré-carrega os primeiros cartões quando a fileira está para entrar na tela.
 * A posição horizontal de cada categoria é guardada e restaurada (como nos apps de streaming).
 */
class AwardsNomineesViewHolder(
    private val binding: AdapterAwardCategoryRowBinding,
    sharedPool: RecyclerView.RecycledViewPool,
    private val nomineeAdapter: NomineeAdapter,
    private val scrollStates: MutableMap<String, Parcelable?>
) : RecyclerView.ViewHolder(binding.root) {

    private val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false).apply {
        initialPrefetchItemCount = PREFETCH_ITEMS
        recycleChildrenOnDetach = true
    }

    private var boundKey: String? = null

    init {
        binding.nomineeRow.apply {
            setHasFixedSize(true)
            setRecycledViewPool(sharedPool)
            layoutManager = this@AwardsNomineesViewHolder.layoutManager
            adapter = nomineeAdapter
            itemAnimator = null
            setItemViewCacheSize(4)
        }
    }

    fun bind(item: AwardItem.Category) {
        binding.categoryTitle.text = item.name
        binding.categoryCount.text = itemView.resources.getQuantityString(
            R.plurals.award_nominees_count, item.nominees.size, item.nominees.size
        )

        val changed = boundKey != item.key
        if (changed) {
            // Outra categoria neste ViewHolder: troca síncrona (submitList(null) + lista nova),
            // para não mostrar por um frame os cartões da categoria anterior.
            nomineeAdapter.submitList(null)
            nomineeAdapter.submitList(item.nominees)
            boundKey = item.key
        } else {
            nomineeAdapter.submitList(item.nominees)
        }

        // Os cartões voltam para o pool ao reciclar a fileira; a posição é restaurada do estado salvo.
        val saved = scrollStates[item.key]
        if (saved != null) {
            layoutManager.onRestoreInstanceState(saved)
        } else if (changed) {
            layoutManager.scrollToPosition(0)
        }
    }

    /** Guarda a posição horizontal antes de o ViewHolder ser reaproveitado. */
    fun saveState() {
        val key = boundKey ?: return
        scrollStates[key] = layoutManager.onSaveInstanceState()
    }

    companion object {
        private const val PREFETCH_ITEMS = 4
    }
}
