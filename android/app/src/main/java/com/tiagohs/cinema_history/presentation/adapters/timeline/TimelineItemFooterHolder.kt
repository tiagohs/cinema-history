package com.tiagohs.cinema_history.presentation.adapters.timeline

import com.tiagohs.cinema_history.databinding.AdapterTimelineFooterBinding
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.fragments.TimelineCallbacks
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineFooter
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show

class TimelineItemFooterHolder(
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    private val onUpClicked: (() -> Unit)?,
    private val numberOfItens: Int,
    val callback: TimelineCallbacks,
    private val binding: AdapterTimelineFooterBinding
) : BaseViewHolder<Timeline>(binding), TimelineLineHolder {

    override val lineFill: android.view.View get() = binding.divisorFill
    override var lastLineProgress: Float = -1f

    init {
        binding.divisorFill.pivotY = 0f
    }


    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timeline = item as? TimelineFooter ?: return

        bindDirectionButtons(timeline)
    }

    private fun bindDirectionButtons(timeline: TimelineFooter) {
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

        binding.upButton.setOnClickListener { onUpClicked?.invoke() }
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
}