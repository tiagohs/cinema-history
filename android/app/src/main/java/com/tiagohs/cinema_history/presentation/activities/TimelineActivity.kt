package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.TimelinePagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.presenter.TimelinePagePresenter
import com.tiagohs.domain.views.TimelinePageView
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.activity_timeline.*
import kotlinx.android.synthetic.main.view_screen_blocked.*
import javax.inject.Inject


class TimelineActivity : BaseActivity(), TimelinePageView {

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    override fun onGetLayoutViewId(): Int = R.layout.activity_timeline
    override fun onGetMenuLayoutId(): Int = R.menu.menu_timeline

    @Inject
    lateinit var presenter: TimelinePagePresenter

    private var adapterPager: TimelinePagerAdapter? = null
    private var startIndex: Int = 0
    private var currentIndex: Int = 0
    private var isFromUniversalLink = false
    private var listOfTimelineIndex: List<Int> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)

        startIndex = intent?.extras?.getString(VIEWPAGER_INDEX)?.toInt() ?: 0
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)

        presenter.fetchTimelineItems()
    }

    override fun onBackPressed() {
        if (isFromUniversalLink) {
            startActivityWithSlideRightToLeftAnimation(HomeActivity.newIntent(this))
            finish()
            return
        }

        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_share -> {
                onShareClicked()
                return true
            }
            else -> return false
        }

    }

    private fun onShareClicked() {
        showScreenBlocked()

        dynamicLinkManager.buildTimelinePageLink(
            currentIndex,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {

        shareContent(
            getString(R.string.share_timeline_description, shorLink),
            getResourceString(R.string.share_title)
        )

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    fun showScreenBlocked() {
        screenBlocked.show()
    }

    fun hideScreenBlocked() {
        screenBlocked.hide()
    }

    override fun bindTimelineIDs(list: List<Int>) {
        this.listOfTimelineIndex = list
        adapterPager = TimelinePagerAdapter(supportFragmentManager, lifecycle, list)

        timelineContentViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = adapterPager
            currentItem = startIndex
        }
        timelineContentViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentIndex = position
            }
        })
    }

    fun isLast() = currentIndex == listOfTimelineIndex.size - 1

    fun isFirst() = currentIndex == 0

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

        fun newIntent(context: Context, startIndex: Int = 0, isFromUniversalLink: Boolean = false): Intent {
            val intent = Intent(context, TimelineActivity::class.java)

            intent.putExtra(VIEWPAGER_INDEX, startIndex)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }
    }
}