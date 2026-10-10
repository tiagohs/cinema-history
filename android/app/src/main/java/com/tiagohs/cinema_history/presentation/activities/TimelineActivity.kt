package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityTimelineBinding
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
import javax.inject.Inject


class TimelineActivity : BaseActivity<ActivityTimelineBinding>(), TimelinePageView {

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    override fun inflateBinding(inflater: LayoutInflater) = ActivityTimelineBinding.inflate(inflater)
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

        // Recriação (rotação, redimensionar janela/multi-janela): volta para a página que estava aberta,
        // não para a do intent (o adapter só é definido depois do carregamento e sobrescreveria a restaurada).
        startIndex = savedInstanceState?.getInt(STATE_CURRENT_INDEX, -1)?.takeIf { it >= 0 }
            ?: intent?.extras?.getString(VIEWPAGER_INDEX)?.toInt() ?: 0
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)

        presenter.fetchTimelineItems()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_INDEX, if (adapterPager != null) currentIndex else startIndex)
    }

    override fun onBackPressed() {
        if (isFromUniversalLink) {
            startActivityWithSlideRightToLeftAnimation(HomeActivity.newIntent(this))
            finish()
            return
        }

        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
        binding.screenBlocked.root.show()
    }

    fun hideScreenBlocked() {
        binding.screenBlocked.root.hide()
    }

    override fun bindTimelineIDs(list: List<Int>) {
        this.listOfTimelineIndex = list
        adapterPager = TimelinePagerAdapter(supportFragmentManager, lifecycle, list)

        binding.timelineContentViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = adapterPager
            currentItem = startIndex
        }

        this.currentIndex = startIndex

        binding.timelineContentViewPager.registerOnPageChangeCallback(object :
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
        val currentPosition = binding.timelineContentViewPager.currentItem

        if (currentPosition < (listOfTimelineIndex.size - 1)) {
            binding.timelineContentViewPager.setCurrentItem(currentPosition + 1, true)
        }

    }

    fun setPreviousPage() {
        val currentPosition = binding.timelineContentViewPager.currentItem

        if (currentPosition > 0 && (listOfTimelineIndex.size - 1) > 0) {
            binding.timelineContentViewPager.setCurrentItem(currentPosition - 1, true)
        }

    }

    companion object {
        private const val STATE_CURRENT_INDEX = "STATE_CURRENT_INDEX"

        const val VIEWPAGER_INDEX = "VIEWPAGER_INDEX"

        fun newIntent(context: Context, startIndex: Int = 0, isFromUniversalLink: Boolean = false): Intent {
            val intent = Intent(context, TimelineActivity::class.java)

            intent.putExtra(VIEWPAGER_INDEX, startIndex)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }
    }
}