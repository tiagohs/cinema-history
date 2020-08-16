package com.tiagohs.cinema_history.presentation.adapters

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Quote
import com.tiagohs.entities.enums.MainTopicItemLayoutType
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.main_topics.DirectorsMainTopic
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.utils.AnimationUtils
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.comingSoonTagContainer
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.contentBackground
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.description
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainImage
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainSubtitle
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.mainTopicsContainer
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.title
import kotlinx.android.synthetic.main.adapter_main_topics_card_full.view.*
import kotlinx.android.synthetic.main.adapter_main_topics_inter_quote.view.*

class MainTopicsAdapter(
    private val mainTopicsType: MainTopicsType,
    list: List<MainTopic>,
    val isDarkMode: Boolean = true
) : BaseAdapter<MainTopic, BaseViewHolder<MainTopic>>(list) {

    var onMainTopicSelected: ((mainTopic: MainTopic, view: View?) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        MainTopicItemLayoutType.QUOTE.ordinal -> R.layout.adapter_main_topics_inter_quote
        MainTopicItemLayoutType.CARD.ordinal -> R.layout.adapter_main_topics_card
        MainTopicItemLayoutType.FULL.ordinal -> R.layout.adapter_main_topics_full
        MainTopicItemLayoutType.CARD_FULL.ordinal -> R.layout.adapter_main_topics_card_full
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BaseViewHolder<MainTopic> =
        when (viewType) {
            MainTopicItemLayoutType.QUOTE.ordinal -> QuoteViewHolder(view)
            MainTopicItemLayoutType.CARD.ordinal -> MainTopicViewHolder(view)
            MainTopicItemLayoutType.FULL.ordinal -> MainTopicViewHolder(view)
            MainTopicItemLayoutType.CARD_FULL.ordinal -> MainTopicViewHolder(view)
            else -> object : BaseViewHolder<MainTopic>(view) {}
        }

    override fun onBindViewHolder(holder: BaseViewHolder<MainTopic>, position: Int) {
        val mainTopic = list.getOrNull(position) ?: return

        when (getItemViewType(position)) {
            MainTopicItemLayoutType.QUOTE.ordinal -> {
                (holder as? QuoteViewHolder)?.bind(mainTopic, position)
            }
            else -> {
                val mainTopicHoder = holder as? MainTopicViewHolder ?: return

                when (mainTopicsType) {
                    MainTopicsType.HISTORY_CINEMA -> {
                        val mainTopicItem = mainTopic as? MainTopicItem ?: return

                        mainTopicHoder.bind(mainTopicItem, position)
                    }
                    MainTopicsType.MIL_MOVIES -> {
                        val milMoviesMainTopic = mainTopic as? MilMoviesMainTopic ?: return

                        mainTopicHoder.bindMillMainTopic(milMoviesMainTopic)
                    }
                    MainTopicsType.DIRECTORS -> {
                        val directorsMainTopic = mainTopic as? DirectorsMainTopic ?: return

                        mainTopicHoder.bindDirectorMainTopic(directorsMainTopic)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].layoutType.ordinal

    override fun onViewAttachedToWindow(holder: BaseViewHolder<MainTopic>) {
        super.onViewAttachedToWindow(holder)

        if (holder is MainTopicViewHolder) {
            holder.setupAnimation()
        }
    }

    inner class MainTopicViewHolder(
        val view: View
    ) : BaseViewHolder<MainTopic>(view) {

        var mainTopicItem: MainTopic? = null

        override fun bind(item: MainTopic, position: Int) {
            super.bind(item, position)

            val mainTopicItem = item as? MainTopicItem ?: return
            val context = containerView.context ?: return

            itemView.mainImage.loadImage(mainTopicItem.image, null)

            mainTopicItem.image.imageStyle?.height?.let {
                itemView.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            itemView.title.setResourceText(mainTopicItem.title)

            itemView.description.setResourceText(mainTopicItem.description)
            itemView.description.show()

            itemView.mainSubtitle.setResourceText(mainTopicItem.subtitle)
            itemView.mainSubtitle.show()

            mainTopicItem.titleColor?.let { itemView.title.setResourceTextColor(mainTopicItem.titleColor) }
            mainTopicItem.titleBackgroundColor?.let {
                itemView.contentBackground.setResourceBackgroundColor(
                    mainTopicItem.titleBackgroundColor
                )
            }
            mainTopicItem.titleColor?.let { itemView.description.setResourceTextColor(mainTopicItem.titleColor) }
            mainTopicItem.titleColor?.let { itemView.mainSubtitle.setResourceTextColor(mainTopicItem.titleColor) }

            itemView.mainTopicsContainer.background = GradientDrawable().apply {
                cornerRadius = 10f
            }

            itemView.mainTopicsContainer.setOnClickListener {
                onMainTopicSelected?.invoke(
                    mainTopicItem,
                    itemView
                )
            }

            if (mainTopicItem.blocked) {
                itemView.mainTopicsContainer.isClickable = false
                itemView.mainTopicsContainer.alpha = 0.3f
                itemView.comingSoonTagContainer.show()
                return
            }

            itemView.mainTopicsContainer.isClickable = true
            itemView.mainTopicsContainer.alpha = 1f
            itemView.comingSoonTagContainer.hide()
        }

        fun bindDirectorMainTopic(mainTopic: DirectorsMainTopic) {
            this.mainTopicItem = mainTopic

            val context = containerView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                itemView.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            itemView.mainImage.loadImage(mainTopic.image, null) {
                itemView.mainImageDegrade.alpha = 1f
            }

            itemView.title.setResourceText(mainTopic.title)
            itemView.mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun bindMillMainTopic(mainTopic: MilMoviesMainTopic) {
            this.mainTopicItem = mainTopic

            val context = containerView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                itemView.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            itemView.mainImage.loadImage(mainTopic.image, null) {
                itemView.mainImageDegrade.alpha = 1f
            }

            itemView.title.setResourceText(mainTopic.title)
            itemView.mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun setupAnimation() {
            val mainTopicItem = mainTopicItem ?: return

            if (mainTopicItem is MainTopicItem) {
                val mainTopicAnimation = mainTopicItem.image.animation ?: return
                val animation = AnimationUtils.createAnimationFromType(
                    mainTopicAnimation.type,
                    mainTopicAnimation.duration
                )

                itemView.mainImage.clearAnimation()
                itemView.mainImage.startAnimation(animation)
            }

        }
    }

    inner class QuoteViewHolder(
        val view: View
    ) : BaseViewHolder<MainTopic>(view) {

        override fun bind(item: MainTopic, position: Int) {
            super.bind(item, position)
            val quote = item as? Quote ?: return
            val quoteColor = if (isDarkMode) R.color.md_white_1000 else R.color.md_black_1000

            itemView.quoteText.setResourceText(quote.quote)
            itemView.quoteTextAuthor.setResourceText(quote.author)

            if (!isDarkMode) {
                itemView.quoteText.setResourceTextColor(R.color.md_black_1000)
            }

            itemView.quoteTop.setImageDrawableColored(R.drawable.ic_quote_bottom_24dp, quoteColor)
            itemView.quoteBottom.setImageDrawableColored(R.drawable.ic_quote_top_24dp, quoteColor)
        }
    }

}