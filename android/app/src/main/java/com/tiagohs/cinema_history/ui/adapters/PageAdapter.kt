package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.enums.ContentType
import com.tiagohs.cinema_history.models.contents.*
import com.tiagohs.cinema_history.ui.adapters.page.*

class PageAdapter(
    val context: Context?,
    val contentList: List<Content>
): RecyclerView.Adapter<BasePageViewHolder>() {

    private var viewHolders: ArrayList<BasePageViewHolder> = ArrayList()

    override fun getItemCount(): Int = contentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePageViewHolder {

        return when (viewType) {
            ContentType.TEXT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                TextViewHolder(context, view)
            }
            ContentType.GIF.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(GifViewHolder.LAYOUT_ID, parent, false)
                GifViewHolder(context, view)
            }
            ContentType.AUDIO_STREAM.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(AudioStreamViewHolder.LAYOUT_ID, parent, false)

                AudioStreamViewHolder(context, view)
            }
            ContentType.IMAGE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(ImageViewHolder.LAYOUT_ID, parent, false)

                ImageViewHolder(context, view)
            }
            ContentType.QUOTE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(QuoteViewHolder.LAYOUT_ID, parent, false)

                QuoteViewHolder(context, view)
            }
            ContentType.SLIDE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(SlideViewHolder.LAYOUT_ID, parent, false)

                SlideViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(TextViewHolder.LAYOUT_ID, parent, false)
                TextViewHolder(context, view)
            }
        }
    }

    override fun onBindViewHolder(holder: BasePageViewHolder, position: Int) {

        when (getItemViewType(position)) {
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
            ContentType.AUDIO_STREAM.ordinal -> {
                val pageAudioStreamContent = contentList[position] as? ContentAudioStream ?: return
                val audioStreamViewHolder = holder as? AudioStreamViewHolder ?: return

                audioStreamViewHolder.bind(pageAudioStreamContent)
            }
            ContentType.IMAGE.ordinal -> {
                val pageImageContent = contentList[position] as? ContentImage ?: return
                val imageStreamViewHolder = holder as? ImageViewHolder ?: return

                imageStreamViewHolder.bind(pageImageContent)
            }
            ContentType.QUOTE.ordinal -> {
                val quoteContent = contentList[position] as? ContentQuote ?: return
                val quoteViewHolder = holder as? QuoteViewHolder ?: return

                quoteViewHolder.bind(quoteContent)
            }
            ContentType.SLIDE.ordinal -> {
                val slideContent = contentList[position] as? ContentSlide ?: return
                val slideViewHolder = holder as? SlideViewHolder ?: return

                slideViewHolder.bind(slideContent)
            }

            else -> {
                val pageTextContent = contentList[position] as? ContentText ?: return
                val textViewHolder = holder as? TextViewHolder ?: return

                textViewHolder.bind(pageTextContent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = contentList.get(position).type.ordinal

    fun onDestroy() {

        viewHolders.forEach {
            if (it is GifViewHolder) {
                it.onDestroy()
            }
        }
    }

}