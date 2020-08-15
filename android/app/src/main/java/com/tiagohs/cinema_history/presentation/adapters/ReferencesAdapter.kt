package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.presentation.adapters.page.BasePageViewHolder
import com.tiagohs.cinema_history.presentation.adapters.references.MediaViewHolder
import com.tiagohs.cinema_history.presentation.adapters.references.TextViewHolder
import com.tiagohs.entities.enums.ReferenceType
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceBook
import com.tiagohs.entities.references.ReferenceText

class ReferencesAdapter(
    val context: Context?,
    val list: List<Reference>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var presentScreen: ((Intent) -> Unit)? = null

    private var viewHolders: ArrayList<BasePageViewHolder> = ArrayList()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ReferenceType.MEDIA.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(MediaViewHolder.LAYOUT_ID, parent, false)
                MediaViewHolder(context, view)
            }
            ReferenceType.TEXT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                TextViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(MediaViewHolder.LAYOUT_ID, parent, false)
                MediaViewHolder(context, view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ReferenceType.MEDIA.ordinal -> {
                val referenceBook = list[position] as? ReferenceBook ?: return
                val mediaViewHolder = holder as? MediaViewHolder ?: return

                mediaViewHolder.bind(referenceBook)
            }
            ReferenceType.TEXT.ordinal -> {
                val referenceText = list[position] as? ReferenceText ?: return
                val textViewHolder = holder as? TextViewHolder ?: return

                textViewHolder.bind(referenceText)
            }
            else -> {
                val referenceBook = list[position] as? ReferenceBook ?: return
                val mediaViewHolder = holder as? MediaViewHolder ?: return

                mediaViewHolder.bind(referenceBook)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list.get(position).type.ordinal
}