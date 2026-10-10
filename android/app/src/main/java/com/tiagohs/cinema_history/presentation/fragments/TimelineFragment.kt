package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.FragmentTimelineBinding
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.domain.presenter.TimelinePresenter
import com.tiagohs.cinema_history.presentation.activities.TimelineActivity
import com.tiagohs.cinema_history.presentation.adapters.TimelineAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.views.TimelineView
import com.bumptech.glide.Glide
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineScrollEffects
import com.tiagohs.cinema_history.presentation.configs.Motion
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show
import javax.inject.Inject

class TimelineFragment: BaseFragment<FragmentTimelineBinding>(), TimelineView, TimelineCallbacks {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentTimelineBinding.inflate(inflater, container, false)
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: TimelinePresenter

    private var timelineId: Int = 1
    private var totalOfTimelines: Int = 0
    private var scrollEffects: TimelineScrollEffects? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getApplicationComponent()?.inject(this)
        (activity as? BaseActivity<*>)?.setupToolbar(binding.toolbar)

        // Tablets: a linha do tempo fica numa coluna centralizada (a marca d'água do ano segue no canto da tela).
        binding.timelineList.limitContentWidth(R.dimen.ls_list_max_width)
        binding.loadView.limitContentWidth(R.dimen.ls_list_max_width)

        presenter.onBindView(this)
        presenter.fetchTimeline(timelineId)
    }

    override fun onDestroyView() {
        scrollEffects?.detach()
        scrollEffects = null
        binding.timelineList.adapter = null

        super.onDestroyView()

        presenter.onUnbindView()
    }

    override fun setupArguments() {
        timelineId = arguments?.getInt(TIMELINE_ID) ?: 0
        totalOfTimelines = arguments?.getInt(TOTAL_TIMELINES) ?: 0
    }

    override fun isFirst() = (activity as? TimelineActivity)?.isFirst() ?: false

    override fun isLast() = (activity as? TimelineActivity)?.isLast() ?: false

    override fun bindTimeline(timelines: TimelineResult) {
        val context = context ?: return
        val motionEnabled = Motion.enabled(context)

        binding.timelineList.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false).apply {
                initialPrefetchItemCount = 2
            }
            setHasFixedSize(true)
            itemAnimator = null
            adapter = TimelineAdapter(
                timelines.timelineList,
                totalOfTimelines,
                timelines.color,
                timelines.titleTextColor,
                this@TimelineFragment,
                Glide.with(this@TimelineFragment),
                motionEnabled
            ).apply {
                onNextClicked = { setNextPage() }
                onPreviousClicked = { setPreviousPage() }
                onUpClicked = { goToFirstItem() }
                onDownClicked = { goToLastItem(timelines.timelineList.size - 1) }
            }
        }

        // Marca d'água do ano na cor da época (translúcida, atrás dos cartões).
        val accent = context.getResourceColor(timelines.color)
        binding.yearWatermark.textColor = (accent and 0x00FFFFFF) or (WATERMARK_COLOR_ALPHA shl 24)

        val effects = scrollEffects ?: TimelineScrollEffects(binding.timelineList, binding.yearWatermark, motionEnabled)
            .also {
                it.attach()
                scrollEffects = it
            }
        effects.reset()

        (timelines.timelineList.firstOrNull() as? TimelineTitle)?.pageTitle?.let { binding.toolbarTitle.text = it }
    }

    private fun setNextPage() {
        (activity as? TimelineActivity)?.setNextPage()
    }

    private fun setPreviousPage() {
        (activity as? TimelineActivity)?.setPreviousPage()
    }

    private fun goToFirstItem() {
        (binding.timelineList.layoutManager as? LinearLayoutManager)?.scrollToPosition(0)
    }

    private fun goToLastItem(lastItemIndex: Int) {
        (binding.timelineList.layoutManager as? LinearLayoutManager)?.scrollToPosition(lastItemIndex)
    }

    override fun startLoading() {
        binding.timelineList.hide()

        binding.loadView.showShimmer(true)
        binding.loadView.hide()
    }

    override fun hideLoading() {
        binding.timelineList.show()

        binding.loadView.stopShimmer()
        binding.loadView.hide()
    }

    companion object {

        private const val WATERMARK_COLOR_ALPHA = 0x59
        const val TIMELINE_ID = "TIMELINE_ID"
        const val TOTAL_TIMELINES = "TOTAL_TIMELINES"

        fun newInstance(timelineId: Int, totalOfTimelines: Int): TimelineFragment {
            val timelineFragment = TimelineFragment()
            timelineFragment.arguments = Bundle().apply {
                putInt(TIMELINE_ID, timelineId)
                putInt(TOTAL_TIMELINES, totalOfTimelines)
            }

            return timelineFragment
        }
    }

}

interface TimelineCallbacks {
    fun isLast(): Boolean
    fun isFirst(): Boolean
}