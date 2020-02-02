package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.cinema_history.presentation.adapters.PagePagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import kotlinx.android.synthetic.main.activity_history_pages.*


class HistoryPagesActivity: BaseActivity() {

    var mainTopic: MainTopicItem? = null
    var sumario: com.tiagohs.entities.Sumario? = null
    var itemSelectedPosition = 0
    var adapterPager: PagePagerAdapter? = null

    override fun onGetLayoutViewId(): Int = R.layout.activity_history_pages
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startLoading()

        setupArguments()
        setupPagesContainer()
        setupFooter()

        Handler().postDelayed( {
            hideLoading()
        }, 1000)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun startLoading() {
        pagesContainer.alpha = 0f

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
        loadView.alpha = 1f
    }

    fun hideLoading() {
        pagesContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        loadView
            .animate()
            .alpha(0f)
            .setDuration(150)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadView.stopShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun setupFooter() {
        val image = mainTopic?.image ?: return
        image.imageStyle?.resize = ImageResize(width = 60.convertIntToDp(this), height = 80.convertIntToDp(this))
        image.imageStyle?.scaleType = "center_crop"

        toolbarImage.loadImage(image)
        toolbarImageCardContainer.setOnClickListener {
            onBackPressed()
        }

        toolbarNextButton.setOnClickListener {
            setNextPage()
        }
    }

    private fun setNextPage() {
        val currentPosition = sumarioContentViewPager.currentItem

        sumarioContentViewPager.setCurrentItem(currentPosition + 1, true)
    }

    fun showFooter() {
        animate(footerContent, 0f, DecelerateInterpolator(2f))
        animate(toolbarNextButton, 0f, DecelerateInterpolator(2f))
        animate(toolbarImageCardContainer, 0f, DecelerateInterpolator(4f))
    }

    fun hideFooter() {
        animate(footerContent, footerContent.height.toFloat(), AccelerateInterpolator(2f))
        animate(toolbarNextButton, toolbarNextButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(toolbarImageCardContainer, toolbarImageCardContainer.height.toFloat() + 10.convertIntToDp(this), AccelerateInterpolator(2f))
    }

    private fun animate(view: View, translationY: Float, interpolator: Interpolator) {
        view.animate()
            .translationY(translationY)
            .setInterpolator(interpolator)
            .start()
    }

    private fun setupArguments() {
        mainTopic = intent.getSerializableExtra(MAIN_TOPIC) as? MainTopicItem
        itemSelectedPosition = intent.getIntExtra(ITEM_SELECTED_POSITION, 0)
    }

    private fun setupPagesContainer() {
        val mainTopic = this.mainTopic ?: return
        adapterPager = PagePagerAdapter(supportFragmentManager, lifecycle, mainTopic.sumarioList)

        sumarioContentViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        sumarioContentViewPager.adapter = adapterPager
        sumarioContentViewPager.currentItem = itemSelectedPosition
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"
        const val ITEM_SELECTED_POSITION = "ITEM_SELECTED_POSITION"

        fun newIntent(context: Context, mainTopic: MainTopicItem, itemSelectedPosition: Int): Intent {
            val intent = Intent(context, HistoryPagesActivity::class.java)

            intent.putExtra(ITEM_SELECTED_POSITION, itemSelectedPosition)
            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}