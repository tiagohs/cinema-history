package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.presenter.TimelinePresenter
import com.tiagohs.cinema_history.ui.adapters.TimelineAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.TimelineView
import kotlinx.android.synthetic.main.activity_timeline.*
import javax.inject.Inject


class TimelineActivity: BaseActivity(), TimelineView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_timeline
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: TimelinePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchTimelineItems()
    }

    override fun bindTimeline(timelines: List<Timeline>) {
        val adapter = TimelineAdapter(this, timelines)

        timelineList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        timelineList.adapter = adapter
    }

    override fun startLoading() {
        timelineList.visibility = View.GONE

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        timelineList.visibility = View.VISIBLE

        loadView.stopShimmer()
        loadView.visibility = View.GONE
    }

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, TimelineActivity::class.java)

            return intent
        }
    }
}