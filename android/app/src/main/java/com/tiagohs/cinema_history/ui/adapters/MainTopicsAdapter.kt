package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.models.MainTopicItem
import kotlinx.android.synthetic.main.adapter_main_topics_full_card.view.*
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.Quote
import kotlinx.android.synthetic.main.adapter_main_topics_inter_quote.view.*

class MainTopicsAdapter(
    val context: Context?,
    val mainTopicList: List<MainTopic>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onMainTopicSelected: ((mainTopic: MainTopicItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            MainTopicItemLayoutType.QUOTE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_inter_quote, parent, false)

                return QuoteViewHolder(view)
            }

            MainTopicItemLayoutType.CARD.ordinal -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_full_card, parent, false)

                return MainTopicViewHolder(view)
            }
            MainTopicItemLayoutType.FULL.ordinal -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_full_screen, parent, false)

                return MainTopicViewHolder(view)
            }
            else -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_full_card, parent, false)

                return MainTopicViewHolder(view)
            }
        }

    }

    override fun getItemCount(): Int = mainTopicList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mainTopic = mainTopicList[position]
        val layoutType = getItemViewType(position)

        when (layoutType) {
            MainTopicItemLayoutType.QUOTE.ordinal -> {
                val quoteHoder = holder as? QuoteViewHolder ?: return
                val quote = mainTopic as? Quote ?: return

                quoteHoder.bind(quote)
            }
            else -> {
                val mainTopicHoder = holder as? MainTopicViewHolder ?: return
                val mainTopic = mainTopic as? MainTopicItem ?: return

                mainTopicHoder.bind(mainTopic)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return mainTopicList[position].layoutType.ordinal
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder is MainTopicViewHolder) {
            holder.setupAnimation()
        }

    }


    inner class MainTopicViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {

        var mainTopicItem: MainTopicItem? = null

        fun bind(mainTopicItem: MainTopicItem) {
            this.mainTopicItem = mainTopicItem

            val context = context ?: return

            when (mainTopicItem.image.imageType) {
                ImageType.ONLINE -> {
                    Picasso.get()
                        .load(mainTopicItem.image.url)
                        .into(itemView.mainImage)
                }
                ImageType.LOCAL -> {
                    val drawable = context.resources
                        .getIdentifier(mainTopicItem.image.url, "drawable", context.packageName)

                    val width = context.resources.configuration.screenWidthDp

                    Picasso.get()
                        .load(drawable)
                        .centerInside()
                        .resize(width, 250.convertIntToDp(context))
                        .into(itemView.mainImage)
                }
            }

            val backgroundColor = context.resources
                .getIdentifier(mainTopicItem.titleBackgroundColor, "color", context.packageName)
            val textColor = context.resources
                .getIdentifier(mainTopicItem.titleColor, "color", context.packageName)

            itemView.title.text = mainTopicItem.title
            itemView.title.setTextColor(context.resources.getColor(textColor))
            itemView.contentBackground.setBackgroundColor(context.resources.getColor(backgroundColor))
            itemView.description.text = mainTopicItem.description
            itemView.description.setTextColor(context.resources.getColor(textColor))
            itemView.mainSubtitle.setTextColor(context.resources.getColor(textColor))
            itemView.mainSubtitle.text = mainTopicItem.subtitle

            val background = GradientDrawable()
            background.cornerRadius = 10f

            itemView.mainTopicsContainer.background = background
            itemView.mainTopicsContainer.setOnClickListener { onMainTopicSelected?.invoke(mainTopicItem) }
        }

        fun setupAnimation() {
            val mainTopicAnimation = mainTopicItem?.image?.animation ?: return
            val animation = AnimationUtils.createAnimationFromType(mainTopicAnimation.type, mainTopicAnimation.duration)

            itemView.mainImage.clearAnimation()
            itemView.mainImage.startAnimation(animation)
        }
    }

    inner class QuoteViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {

        fun bind(quote: Quote) {
            itemView.quoteText.text = quote.quote
            itemView.quoteTextAuthor.text = quote.author
        }
    }
}