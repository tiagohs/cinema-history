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

class TimelineFragment: BaseFragment(), TimelineView {

    override fun getViewID(): Int = R.layout.fragment_timeline
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: TimelinePresenter

    private var timelineId: Int = 1

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
    }

    override fun bindTimeline(timelines: TimelineResult) {
        timelineList.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            timelineList.adapter = TimelineAdapter(timelines.timelineList, timelines.color, timelines.titleTextColor).apply {
                onNextClicked = { setNextPage() }
                onPreviousClicked = { setPreviousPage() }
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

        fun newInstance(timelineId: Int): TimelineFragment {
            val timelineFragment = TimelineFragment()
            timelineFragment.arguments = Bundle().apply {
                putInt(TIMELINE_ID, timelineId)
            }

            return timelineFragment
        }
    }
}