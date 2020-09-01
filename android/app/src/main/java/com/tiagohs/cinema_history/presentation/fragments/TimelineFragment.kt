package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.domain.presenter.TimelinePresenter
import com.tiagohs.cinema_history.presentation.activities.TimelineActivity
import com.tiagohs.cinema_history.presentation.adapters.TimelineAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.views.TimelineView
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.fragment_timeline.*
import javax.inject.Inject

class TimelineFragment: BaseFragment(), TimelineView, TimelineCallbacks {

    override fun getViewID(): Int = R.layout.fragment_timeline
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: TimelinePresenter

    private var timelineId: Int = 1
    private var totalOfTimelines: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getApplicationComponent()?.inject(this)
        (activity as? BaseActivity)?.setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchTimeline(timelineId)
    }

    override fun onDestroyView() {
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

        timelineList.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            timelineList.adapter = TimelineAdapter(timelines.timelineList, totalOfTimelines, timelines.color, timelines.titleTextColor, this@TimelineFragment).apply {
                onNextClicked = { setNextPage() }
                onPreviousClicked = { setPreviousPage() }
                onUpClicked = { goToFirstItem() }
                onDownClicked = { goToLastItem(timelines.timelineList.size - 1) }
            }
        }

        (timelines.timelineList.firstOrNull() as? TimelineTitle)?.pageTitle?.let { toolbarTitle.text = it }
    }

    private fun setNextPage() {
        (activity as? TimelineActivity)?.setNextPage()
    }

    private fun setPreviousPage() {
        (activity as? TimelineActivity)?.setPreviousPage()
    }

    private fun goToFirstItem() {
        (timelineList.layoutManager as? LinearLayoutManager)?.scrollToPosition(0)
    }

    private fun goToLastItem(lastItemIndex: Int) {
        (timelineList.layoutManager as? LinearLayoutManager)?.scrollToPosition(lastItemIndex)
    }

    override fun startLoading() {
        timelineList.hide()

        loadView.showShimmer(true)
        loadView.hide()
    }

    override fun hideLoading() {
        timelineList.show()

        loadView.stopShimmer()
        loadView.hide()
    }

    companion object {

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