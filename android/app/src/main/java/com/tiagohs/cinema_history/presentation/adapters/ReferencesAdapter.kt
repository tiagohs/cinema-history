package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.entities.contents.*
import com.tiagohs.cinema_history.presentation.adapters.page.*
import com.tiagohs.cinema_history.presentation.adapters.references.BookViewHolder
import com.tiagohs.entities.enums.ReferenceType
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceBook

class ReferencesAdapter(
    val context: Context?,
    val list: List<Reference>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var presentScreen: ((Intent) -> Unit)? = null

    private var viewHolders: ArrayList<BasePageViewHolder> = ArrayList()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ReferenceType.BOOK.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                BookViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                BookViewHolder(context, view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ReferenceType.BOOK.ordinal -> {
                val referenceBook = list[position] as? ReferenceBook ?: return
                val bookViewHolder = holder as? BookViewHolder ?: return

                bookViewHolder.bind(referenceBook)
            }
            else -> {
                val referenceBook = list[position] as? ReferenceBook ?: return
                val bookViewHolder = holder as? BookViewHolder ?: return

                bookViewHolder.bind(referenceBook)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list.get(position).type.ordinal
}