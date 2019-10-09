package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.enums.ContentType
import com.tiagohs.cinema_history.models.contents.Content
import com.tiagohs.cinema_history.models.contents.ContentGif
import com.tiagohs.cinema_history.models.contents.ContentText
import com.tiagohs.cinema_history.ui.adapters.page.GifViewHolder
import com.tiagohs.cinema_history.ui.adapters.page.TextViewHolder

class PageAdapter(
    val context: Context?,
    val contentList: List<Content>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = contentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ContentType.TEXT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                TextViewHolder(context, view)
            }
            ContentType.GIF.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(GifViewHolder.LAYOUT_ID, parent, false)
                GifViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                TextViewHolder(context, view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        when (viewType) {
            ContentType.TEXT.ordinal -> {
                val pageTextContent = contentList[position] as? ContentText ?: return
                val textViewHolder = holder as? TextViewHolder ?: return

                textViewHolder.bind(pageTextContent)
            }
            ContentType.GIF.ordinal -> {
                val pageGifContent = contentList[position] as? ContentGif ?: return
                val gifViewHolder = holder as? GifViewHolder ?: return

                gifViewHolder.bind(pageGifContent)
            }
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int = contentList.get(position).type.ordinal
}