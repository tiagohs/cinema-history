package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityHomeBinding
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.HomeAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.presenter.HomePresenter
import com.tiagohs.domain.views.HomeView
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import javax.inject.Inject


class HomeActivity : BaseActivity<ActivityHomeBinding>(), HomeView {

    @Inject
    lateinit var presenter: HomePresenter

    override fun inflateBinding(inflater: LayoutInflater) = ActivityHomeBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = R.menu.menu_main

    private var adapter: HomeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchHomeContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                startActivityWithSlideRightToLeftAnimation(SettingActivity.newIntent(this))
                return true
            }
            R.id.action_glossary -> {
                startActivityWithSlideRightToLeftAnimation(GlossaryActivity.newIntent(this))
                return true
            }
            R.id.action_about -> {
                startActivityWithSlideRightToLeftAnimation(AboutActivty.newIntent(this))
                return true
            }
            R.id.action_references -> {
                startActivityWithSlideRightToLeftAnimation(ReferenceActivity.newIntent(this))
                return true
            }

            else -> return false
        }

    }

    override fun setupContentView() {
        setupToolbar(binding.toolbar, displayHomeAsUpEnabled = false, displayShowTitleEnabled = false)
    }

    override fun bindHomeContent(homeContentList: List<HomeContentItem>) {
        adapter = HomeAdapter(homeContentList).apply {
            onItemClicked = { onHomeItemClicked(it) }
        }

        binding.homeViewPager.apply {
            adapter = this@HomeActivity.adapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1

            // setPageTransformer(SliderTransformer(4))
            setPageTransformer { page, position ->
                page.apply {
                    if (position >= -1 && position <= 1) { // [-1,1]
                        page.findViewById<TextView>(R.id.title1)?.translationX =
                            (position) * (width / 4).toFloat()
                        page.findViewById<TextView>(R.id.title2)?.translationX =
                            (position) * (width / 2).toFloat()
                    } else {
                        page.findViewById<TextView>(R.id.title2)?.translationX =
                            (position) * (width / 4).toFloat()
                        page.findViewById<TextView>(R.id.title2)?.translationX =
                            (position) * (width / 2).toFloat()
                    }
                }
            }

            binding.contentIndicator.attachToViewPager2(this)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.contentIndicator.onPageSelected(position)

                    if (binding.homeViewPager.currentItem < homeContentList.size - 1) {
                        binding.nextButton.show()
                    } else {
                        binding.nextButton.hide()
                    }

                    if (binding.homeViewPager.currentItem > 0) {
                        binding.previousButton.show()
                    } else {
                        binding.previousButton.hide()
                    }
                }
            })
        }

        binding.nextButton.setOnClickListener {
            val currentPosition = binding.homeViewPager.currentItem

            binding.homeViewPager.setCurrentItem(currentPosition + 1, true)
        }
        binding.previousButton.setOnClickListener {
            val currentPosition = binding.homeViewPager.currentItem

            binding.homeViewPager.setCurrentItem(currentPosition - 1, true)
        }
    }

    private fun onHomeItemClicked(homeContentItem: HomeContentItem) {
        if (homeContentItem.mainTopicType == MainTopicsType.TIMELINE) {
            startActivityWithSlideRightToLeftAnimation(TimelineActivity.newIntent(this))
            return
        }

        startActivityWithSlideRightToLeftAnimation(
            MainTopicsActivity.newIntent(
                homeContentItem.mainTopicType,
                this,
                darkMode = homeContentItem.darkMode
            )
        )
    }

    override fun startLoading() {
        binding.loadView.startShimmer()
    }

    override fun hideLoading() {
        binding.loadView.stopShimmer()

        binding.contentContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}
