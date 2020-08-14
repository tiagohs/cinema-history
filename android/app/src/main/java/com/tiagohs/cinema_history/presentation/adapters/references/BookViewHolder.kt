package com.tiagohs.cinema_history.presentation.adapters.references

import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.entities.references.ReferenceBook
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.adapter_page_text.view.*

class BookViewHolder(
    val context: Context?,
    val view: View
): RecyclerView.ViewHolder(view) {

    fun bind(referenceBook: ReferenceBook) {
        val context = context ?: return

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_reference_book
    }
}
