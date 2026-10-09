package com.tiagohs.cinema_history.presentation.adapters.timeline

import com.tiagohs.cinema_history.databinding.AdapterTimelineTitleBinding
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.fragments.TimelineCallbacks
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show

class TimelineTitleViewHolder(
    val color: String,
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    private val onDownClicked: (() -> Unit)? = null,
    private val numberOfItens: Int,
    val callback: TimelineCallbacks,
    private val binding: AdapterTimelineTitleBinding
) : BaseViewHolder<Timeline>(binding), TimelineLineHolder {

    override val lineFill: android.view.View get() = binding.divisorFill
    override var lastLineProgress: Float = -1f


    init {
        bindColors()
        binding.divisorFill.pivotY = 0f
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timeline = item as? TimelineTitle ?: return

        binding.title1.setResourceStyledText(timeline.title)

        bindDirectionButtons(timeline)

        if (timeline.comingSoon == true) {
            binding.comingSoonTagContainer.show()
            return
        }

        binding.comingSoonTagContainer.hide()
    }

    private fun bindDirectionButtons(timeline: TimelineTitle) {
        bindButton(
            binding.nextContainer,
            binding.nextText,
            timeline.next,
            timeline.next != null && !callback.isLast()
        ) {
            onNextClicked?.invoke()
        }
        bindButton(
            binding.previousContainer,
            binding.previousText,
            timeline.previous,
            timeline.previous != null && !callback.isFirst()
        ) {
            onPreviousClicked?.invoke()
        }

        binding.downButton.setOnClickListener { onDownClicked?.invoke() }
    }

    private fun bindButton(
        view: ConstraintLayout?,
        textView: TextView,
        text: String?,
        isToShow: Boolean,
        onClicked: () -> Unit
    ) {
        if (!isToShow) {
            view.hide()
            return
        }

        view.show()
        view?.setOnClickListener { onClicked.invoke() }

        textView.setResourceStyledText(text)
    }

    private fun bindColors() {
        val context = itemView.context ?: return
        val colorRes = context.getResourceColor(color)

        binding.textLine.setCardBackgroundColor(colorRes)
        binding.title2.setTextColor(colorRes)
    }
}