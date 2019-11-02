package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.presenter.TimelinePagePresenter
import com.tiagohs.cinema_history.ui.adapters.TimelinePagerAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.TimelinePageView
import kotlinx.android.synthetic.main.activity_timeline.*
import javax.inject.Inject


class TimelineActivity: BaseActivity(), TimelinePageView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_timeline
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: TimelinePagePresenter

    var adapterPager: TimelinePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchTimelineItems()
    }

    override fun bindTimelineIDs(list: List<Int>) {
        adapterPager = TimelinePagerAdapter(supportFragmentManager, lifecycle, list)

        timelineContentViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        timelineContentViewPager.adapter = adapterPager
    }

    fun setNextPage() {
        val currentPosition = timelineContentViewPager.currentItem

        if (currentPosition < timelineContentViewPager.size) {
            timelineContentViewPager.setCurrentItem(currentPosition + 1, true)
        }

    }

    fun setPreviousPage() {
        val currentPosition = timelineContentViewPager.currentItem

        if (currentPosition > 0 && timelineContentViewPager.size > 0) {
            timelineContentViewPager.setCurrentItem(currentPosition - 1, true)
        }

    }

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, TimelineActivity::class.java)

            return intent
        }
    }
}