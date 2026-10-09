package com.tiagohs.cinema_history.presentation.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewbinding.ViewBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
import com.tiagohs.cinema_history.databinding.AdapterMainTopicsCardBinding
import com.tiagohs.cinema_history.databinding.AdapterMainTopicsCardFullBinding
import com.tiagohs.cinema_history.databinding.AdapterMainTopicsFullBinding
import com.tiagohs.cinema_history.databinding.AdapterMainTopicsInterQuoteBinding
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Quote
import com.tiagohs.entities.enums.MainTopicItemLayoutType
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.main_topics.*
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.utils.AnimationUtils

class MainTopicsAdapter(
    private val mainTopicsType: MainTopicsType,
    list: List<MainTopic>,
    val isDarkMode: Boolean = true
) : BaseAdapter<MainTopic, BaseViewHolder<MainTopic>>(list) {

    var onMainTopicSelected: ((mainTopic: MainTopic, view: View?) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<MainTopic> =
        when (viewType) {
            MainTopicItemLayoutType.QUOTE.ordinal -> QuoteViewHolder(AdapterMainTopicsInterQuoteBinding.inflate(inflater, parent, false))
            MainTopicItemLayoutType.CARD.ordinal -> AdapterMainTopicsCardBinding.inflate(inflater, parent, false)
                .let { MainTopicViewHolder(it, MainTopicViews.from(it)) }
            MainTopicItemLayoutType.FULL.ordinal -> AdapterMainTopicsFullBinding.inflate(inflater, parent, false)
                .let { MainTopicViewHolder(it, MainTopicViews.from(it)) }
            MainTopicItemLayoutType.CARD_FULL.ordinal -> AdapterMainTopicsCardFullBinding.inflate(inflater, parent, false)
                .let { MainTopicViewHolder(it, MainTopicViews.from(it)) }
            else -> object : BaseViewHolder<MainTopic>(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
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
                    MainTopicsType.AWARDS -> {
                        val awardMainTopic = mainTopic as? AwardMainTopic ?: return

                        mainTopicHoder.bindAwardsMainTopic(awardMainTopic)
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
        binding: ViewBinding,
        private val views: MainTopicViews
    ) : BaseViewHolder<MainTopic>(binding) {

        var mainTopicItem: MainTopic? = null

        override fun bind(item: MainTopic, position: Int) {
            super.bind(item, position)
            this.mainTopicItem = item

            val mainTopicItem = item as? MainTopicItem ?: return
            val context = itemView.context ?: return

            views.mainImage.loadImage(mainTopicItem.image, null)

            mainTopicItem.image.imageStyle?.height?.let {
                views.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            views.title.setResourceText(mainTopicItem.title)

            views.description.setResourceText(mainTopicItem.description)
            views.description.show()

            views.mainSubtitle.setResourceText(mainTopicItem.subtitle)
            views.mainSubtitle.show()

            mainTopicItem.titleColor?.let { views.title.setResourceTextColor(mainTopicItem.titleColor) }
            mainTopicItem.titleBackgroundColor?.let {
                views.contentBackground.setResourceBackgroundColor(
                    mainTopicItem.titleBackgroundColor
                )
            }
            mainTopicItem.titleColor?.let {
                views.description.setResourceTextColor(mainTopicItem.titleColor)
                views.mainSubtitle.setResourceTextColor(mainTopicItem.titleColor)
                views.nextButton.setResourceImageColor(mainTopicItem.titleColor)
            }

            views.mainTopicsContainer.background = GradientDrawable().apply {
                cornerRadius = 10f
            }

            views.mainTopicsContainer.setOnClickListener {
                onMainTopicSelected?.invoke(
                    mainTopicItem,
                    itemView
                )
            }

            bindButtons(mainTopicItem)
        }

        private fun bindButtons(mainTopicItem: MainTopicItem) {
            if (!mainTopicItem.isNew) {
                bindBlockedButton(mainTopicItem)
            }

            if (!mainTopicItem.blocked) {
                bindIsNewButton(mainTopicItem)
            }
        }

        private fun bindBlockedButton(mainTopicItem: MainTopicItem) {
            if (mainTopicItem.blocked) {
                views.mainTopicsContainer.isClickable = false
                views.mainTopicsContainer.alpha = 0.3f
                views.comingSoonTagContainer.show()

                views.comingSoonTag.setResourceText(R.string.comingsoon)
                views.comingSoonTagContainer.setCardBackgroundColor(itemView.context.getResourceColor(R.color.md_red_500))
                return
            }

            views.mainTopicsContainer.isClickable = true
            views.mainTopicsContainer.alpha = 1f
            views.comingSoonTagContainer.hide()
        }

        private fun bindIsNewButton(mainTopicItem: MainTopicItem) {
            if (mainTopicItem.isNew) {
                views.mainTopicsContainer.isClickable = true
                views.mainTopicsContainer.alpha = 1f
                views.comingSoonTagContainer.show()

                views.comingSoonTag.setResourceText(R.string.is_new)
                views.comingSoonTagContainer.setCardBackgroundColor(itemView.context.getResourceColor(R.color.md_green_500))
                return
            }

            views.comingSoonTagContainer.hide()
        }

        fun bindDirectorMainTopic(mainTopic: DirectorsMainTopic) {
            this.mainTopicItem = mainTopic

            val context = itemView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                views.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            views.mainImage.loadImage(mainTopic.image, null) {
                views.mainImageDegrade?.alpha = 1f
            }

            views.title.setResourceText(mainTopic.title)
            views.mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun bindAwardsMainTopic(mainTopic: AwardMainTopic) {
            this.mainTopicItem = mainTopic

            val context = itemView.context ?: return

            mainTopic.image?.imageStyle?.height?.let {
                views.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            views.mainImage.loadImage(mainTopic.image, null) {
                views.mainImageDegrade?.alpha = 1f
            }

            views.title.setResourceText(mainTopic.name)
            views.mainTopicsContainer.setOnClickListener {
                val mainTopicItem = mainTopicItem ?: return@setOnClickListener

                onMainTopicSelected?.invoke(mainTopicItem, null)
            }
        }

        fun bindMillMainTopic(mainTopic: MilMoviesMainTopic) {
            this.mainTopicItem = mainTopic

            val context = itemView.context ?: return

            mainTopic.image.imageStyle?.height?.let {
                views.mainImage.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    it.convertIntToDp(context)
                )
            }

            views.mainImage.loadImage(mainTopic.image, null) {
                views.mainImageDegrade?.alpha = 1f
            }

            views.title.setResourceText(mainTopic.title)
            views.mainTopicsContainer.setOnClickListener {
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

                views.mainImage.clearAnimation()
                views.mainImage.startAnimation(animation)
            }

        }
    }

    inner class QuoteViewHolder(
        private val binding: AdapterMainTopicsInterQuoteBinding
    ) : BaseViewHolder<MainTopic>(binding) {

        override fun bind(item: MainTopic, position: Int) {
            super.bind(item, position)
            val quote = item as? Quote ?: return
            // Lista clara segue o tema do app (texto escuro no claro, claro no escuro).
            val quoteColor = if (isDarkMode) R.color.md_white_1000 else R.color.daynight_text_primary

            binding.quoteText.setResourceText(quote.quote)
            binding.quoteTextAuthor.setResourceText(quote.author)

            if (!isDarkMode) {
                binding.quoteText.setResourceTextColor(R.color.daynight_text_primary)
            }

            binding.quoteTop.setResourceImageColor(quoteColor)
            binding.quoteBottom.setResourceImageColor(quoteColor)
        }
    }

}

class MainTopicViews(
    val mainTopicsContainer: ConstraintLayout,
    val mainImage: ImageView,
    val mainImageDegrade: View?,
    val contentBackground: ConstraintLayout,
    val mainSubtitle: TextView,
    val title: TextView,
    val nextButton: ImageView,
    val description: TextView,
    val comingSoonTagContainer: CardView,
    val comingSoonTag: TextView
) {

    companion object {

        fun from(binding: AdapterMainTopicsCardBinding) = MainTopicViews(
            binding.mainTopicsContainer, binding.mainImage, null, binding.contentBackground,
            binding.mainSubtitle, binding.title, binding.nextButton, binding.description,
            binding.comingSoonTagContainer, binding.comingSoonTag
        )

        fun from(binding: AdapterMainTopicsFullBinding) = MainTopicViews(
            binding.mainTopicsContainer, binding.mainImage, null, binding.contentBackground,
            binding.mainSubtitle, binding.title, binding.nextButton, binding.description,
            binding.comingSoonTagContainer, binding.comingSoonTag
        )

        fun from(binding: AdapterMainTopicsCardFullBinding) = MainTopicViews(
            binding.mainTopicsContainer, binding.mainImage, binding.mainImageDegrade, binding.contentBackground,
            binding.mainSubtitle, binding.title, binding.nextButton, binding.description,
            binding.comingSoonTagContainer, binding.comingSoonTag
        )
    }
}
