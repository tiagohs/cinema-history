package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.domain.presenter.TimelinePagePresenter
import com.tiagohs.cinema_history.presentation.adapters.TimelinePagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.views.TimelinePageView
import com.tiagohs.helpers.extensions.startActivityWithSlideAnimation
import kotlinx.android.synthetic.main.activity_timeline.*
import javax.inject.Inject


class TimelineActivity: BaseActivity(), TimelinePageView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_timeline
    override fun onGetMenuLayoutId(): Int = R.menu.menu_timeline

    @Inject
    lateinit var presenter: TimelinePagePresenter

    private var adapterPager: TimelinePagerAdapter? = null
    private var startIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)

        startIndex = intent?.extras?.getString(VIEWPAGER_INDEX)?.toInt() ?: 0

        presenter.fetchTimelineItems()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_share -> {

                return true
            }
            else -> return false
        }

    }

    override fun bindTimelineIDs(list: List<Int>) {
        adapterPager = TimelinePagerAdapter(supportFragmentManager, lifecycle, list)

        timelineContentViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = adapterPager
            currentItem = startIndex
        }
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

        const val VIEWPAGER_INDEX = "VIEWPAGER_INDEX"

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, TimelineActivity::class.java)

            return intent
        }
    }
}