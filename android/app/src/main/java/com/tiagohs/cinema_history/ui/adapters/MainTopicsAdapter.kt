package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.helpers.extensions.*
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.models.Quote
import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.*
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.contentBackground
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.description
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainImage
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainSubtitle
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainTopicsContainer
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.title
import kotlinx.android.synthetic.main.adapter_main_topics_card_full.view.*
import kotlinx.android.synthetic.main.adapter_main_topics_inter_quote.view.*

class MainTopicsAdapter(
    val context: Context?,
    val mainTopicsType: MainTopicsType,
    val mainTopicList: List<MainTopic>,
    val isDarkMode: Boolean = true
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onMainTopicSelected: ((mainTopic: MainTopic, view: View?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MainTopicItemLayoutType.QUOTE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_inter_quote, parent, false)

                 QuoteViewHolder(view)
            }

            MainTopicItemLayoutType.CARD.ordinal -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_card, parent, false)

                 MainTopicViewHolder(view)
            }
            MainTopicItemLayoutType.FULL.ordinal -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_full, parent, false)

                 MainTopicViewHolder(view)
            }
            MainTopicItemLayoutType.CARD_FULL.ordinal -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_card_full, parent, false)

                 MainTopicViewHolder(view)
            }
            else -> {
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics_card, parent, false)

                 MainTopicViewHolder(view)
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

                when (mainTopicsType) {
                    MainTopicsType.HISTORY_CINEMA -> {
                        val mainTopicItem = mainTopic as? MainTopicItem ?: return

                        mainTopicHoder.bind(mainTopicItem)
                    }
                    MainTopicsType.MIL_MOVIES -> {
                        val milMoviesMainTopic = mainTopic as? MilMoviesMainTopic ?: return

                        mainTopicHoder.bindMillMainTopic(milMoviesMainTopic)
                    }
                }

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

        var mainTopicItem: MainTopic? = null

        fun bindMillMainTopic(mainTopic: MilMoviesMainTopic) {
            this.mainTopicItem = mainTopic

            val context = context ?: return

            mainTopic.image.imageStyle?.height?.let {
                val imageListLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))

                itemView.mainImage.layoutParams = imageListLayoutParams
            }

            itemView.mainImage.loadImage(mainTopic.image, null) {
                itemView.mainImageDegrade.alpha = 1f
            }

            itemView.title.text = mainTopic.title
            itemView.mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun bind(mainTopicItem: MainTopicItem) {
            this.mainTopicItem = mainTopicItem

            val context = context ?: return

            itemView.mainImage.loadImage(mainTopicItem.image, null)

            mainTopicItem.image.imageStyle?.height?.let {
                val imageListLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))

                itemView.mainImage.layoutParams = imageListLayoutParams
            }

            itemView.title.text = mainTopicItem.title

            itemView.description.text = mainTopicItem.description
            itemView.description.visibility = View.VISIBLE

            itemView.mainSubtitle.text = mainTopicItem.subtitle
            itemView.mainSubtitle.visibility = View.VISIBLE

            mainTopicItem.titleColor?.let { itemView.title.setTextColor(context.getResourceColor(mainTopicItem.titleColor)) }
            mainTopicItem.titleBackgroundColor?.let { itemView.contentBackground.setBackgroundColor(context.getResourceColor(mainTopicItem.titleBackgroundColor)) }
            mainTopicItem.titleColor?.let { itemView.description.setTextColor(context.getResourceColor(mainTopicItem.titleColor)) }
            mainTopicItem.titleColor?.let { itemView.mainSubtitle.setTextColor(context.getResourceColor(mainTopicItem.titleColor)) }

            val background = GradientDrawable()
            background.cornerRadius = 10f

            itemView.mainTopicsContainer.background = background
            itemView.mainTopicsContainer.setOnClickListener { onMainTopicSelected?.invoke(mainTopicItem, itemView) }
        }

        fun setupAnimation() {
            val mainTopicItem = mainTopicItem ?: return

            if (mainTopicItem is MainTopicItem) {
                val mainTopicAnimation = mainTopicItem.image.animation ?: return
                val animation = AnimationUtils.createAnimationFromType(mainTopicAnimation.type, mainTopicAnimation.duration)

                itemView.mainImage.clearAnimation()
                itemView.mainImage.startAnimation(animation)
            }

        }
    }

    inner class QuoteViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {

        fun bind(quote: Quote) {
            val context = context ?: return
            val quoteColor =  if (isDarkMode) R.color.md_white_1000 else R.color.md_black_1000

            itemView.quoteText.text = quote.quote
            itemView.quoteTextAuthor.text = quote.author

            if (!isDarkMode) {
                itemView.quoteText.setTextColor(context.getResourceColor(R.color.md_black_1000))
            }

            itemView.quoteTop.setImageDrawableColored(R.drawable.ic_quote_bottom_24dp, quoteColor)
            itemView.quoteBottom.setImageDrawableColored(R.drawable.ic_quote_top_24dp, quoteColor)
        }
    }
}