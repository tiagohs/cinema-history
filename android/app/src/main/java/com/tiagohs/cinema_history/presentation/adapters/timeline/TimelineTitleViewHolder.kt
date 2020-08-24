package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_timeline_title.*

class TimelineTitleViewHolder(
    val color: String,
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    private val onDownClicked: (() -> Unit)? = null,
    private val numberOfItens: Int,
    view: View
) : BaseViewHolder<Timeline>(view) {

    init {
        bindColors()
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timeline = item as? TimelineTitle ?: return

        title1.setResourceStyledText(timeline.title)

        bindDirectionButtons(timeline)

        if (timeline.comingSoon == true) {
            comingSoonTagContainer.show()
            return
        }

        comingSoonTagContainer.hide()
    }

    private fun bindDirectionButtons(timeline: TimelineTitle) {
        bindButton(nextContainer, timeline.next != null && positionItem < numberOfItens) {
            onNextClicked?.invoke()
        }
        bindButton(previousContainer, timeline.previous != null && positionItem == 0) {
            onPreviousClicked?.invoke()
        }

        downButton.setOnClickListener { onDownClicked?.invoke() }
    }

    private fun bindButton(view: ConstraintLayout?, isToShow: Boolean, onClicked: () -> Unit) {
        if (!isToShow) {
            view.hide()
            return
        }

        view.show()
        view?.setOnClickListener { onClicked.invoke() }
    }

    private fun bindColors() {
        val context = containerView.context ?: return
        val colorRes = context.getResourceColor(color)

        textLine.setCardBackgroundColor(colorRes)
        title2.setTextColor(colorRes)
    }
}