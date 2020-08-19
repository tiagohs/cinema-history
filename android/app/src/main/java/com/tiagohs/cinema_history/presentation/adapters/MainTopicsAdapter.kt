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
import kotlinx.android.synthetic.main.adapter_main_topics_card.comingSoonTagContainer
import kotlinx.android.synthetic.main.adapter_main_topics_card.contentBackground
import kotlinx.android.synthetic.main.adapter_main_topics_card.description
import kotlinx.android.synthetic.main.adapter_main_topics_card.mainImage
import kotlinx.android.synthetic.main.adapter_main_topics_card.mainSubtitle
import kotlinx.android.synthetic.main.adapter_main_topics_card.mainTopicsContainer
import kotlinx.android.synthetic.main.adapter_main_topics_card.title
import kotlinx.android.synthetic.main.adapter_main_topics_card_full.*
import kotlinx.android.synthetic.main.adapter_main_topics_inter_quote.*

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

            mainImage.loadImage(mainTopicItem.image, null)

            mainTopicItem.image.imageStyle?.height?.let {
                mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            title.setResourceText(mainTopicItem.title)

            description.setResourceText(mainTopicItem.description)
            description.show()

            mainSubtitle.setResourceText(mainTopicItem.subtitle)
            mainSubtitle.show()

            mainTopicItem.titleColor?.let { title.setResourceTextColor(mainTopicItem.titleColor) }
            mainTopicItem.titleBackgroundColor?.let {
                contentBackground.setResourceBackgroundColor(
                    mainTopicItem.titleBackgroundColor
                )
            }
            mainTopicItem.titleColor?.let { description.setResourceTextColor(mainTopicItem.titleColor) }
            mainTopicItem.titleColor?.let { mainSubtitle.setResourceTextColor(mainTopicItem.titleColor) }

            mainTopicsContainer.background = GradientDrawable().apply {
                cornerRadius = 10f
            }

            mainTopicsContainer.setOnClickListener {
                onMainTopicSelected?.invoke(
                    mainTopicItem,
                    itemView
                )
            }

            if (mainTopicItem.blocked) {
                mainTopicsContainer.isClickable = false
                mainTopicsContainer.alpha = 0.3f
                comingSoonTagContainer.show()
                return
            }

            mainTopicsContainer.isClickable = true
            mainTopicsContainer.alpha = 1f
            comingSoonTagContainer.hide()
        }

        fun bindDirectorMainTopic(mainTopic: DirectorsMainTopic) {
            this.mainTopicItem = mainTopic

            val context = containerView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            mainImage.loadImage(mainTopic.image, null) {
                mainImageDegrade.alpha = 1f
            }

            title.setResourceText(mainTopic.title)
            mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun bindMillMainTopic(mainTopic: MilMoviesMainTopic) {
            this.mainTopicItem = mainTopic

            val context = containerView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            mainImage.loadImage(mainTopic.image, null) {
                mainImageDegrade.alpha = 1f
            }

            title.setResourceText(mainTopic.title)
            mainTopicsContainer.setOnClickListener {
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

                mainImage.clearAnimation()
                mainImage.startAnimation(animation)
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

            quoteText.setResourceText(quote.quote)
            quoteTextAuthor.setResourceText(quote.author)

            if (!isDarkMode) {
                quoteText.setResourceTextColor(R.color.md_black_1000)
            }

            quoteTop.setImageDrawableColored(R.drawable.ic_quote_bottom_24dp, quoteColor)
            quoteBottom.setImageDrawableColored(R.drawable.ic_quote_top_24dp, quoteColor)
        }
    }

}