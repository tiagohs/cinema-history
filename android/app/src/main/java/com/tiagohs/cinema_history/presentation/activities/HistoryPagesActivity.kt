package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.PagePagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.activity_history_pages.*


class HistoryPagesActivity : BaseActivity() {

    var mainTopic: MainTopicItem? = null
    var adapterPager: PagePagerAdapter? = null

    private var itemSelectedPosition = 0

    override fun onGetLayoutViewId(): Int = R.layout.activity_history_pages
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startLoading()

        setupArguments()
        setupPagesContainer()
        setupFooter()

        Handler().postDelayed({
            hideLoading()
        }, 1000)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun startLoading() {
        pagesContainer.alpha = 0f

        loadView.startShimmer()
        loadView.show()
        loadView.alpha = 1f
    }

    private fun hideLoading() {
        pagesContainer
            ?.animate()
            ?.alpha(1f)
            ?.setDuration(200)
            ?.setInterpolator(DecelerateInterpolator(2f))
            ?.start()

        loadView
            ?.animate()
            ?.alpha(0f)
            ?.setDuration(150)
            ?.setInterpolator(AccelerateInterpolator(2f))
            ?.setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadView?.hideShimmer()
                    loadView?.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            ?.start()
    }

    private fun setupFooter() {
        val image = mainTopic?.image ?: return
        image.imageStyle?.resize = ImageResize(
            width = 60.convertIntToDp(this), height = 80.convertIntToDp(
                this
            )
        )
        image.imageStyle?.scaleType = "center_crop"

        toolbarImage.loadImage(image)
        toolbarImageCardContainer.setOnClickListener {
            onBackPressed()
        }

        toolbarNextButton.setOnClickListener {
            setNextPage()
        }

        shareButton.setOnClickListener { onShareClicked() }
    }

    private fun onShareClicked() {
        val baseUrl = Uri.parse("https://thshoc.link/?mainTopicId=1")

        val shortLink = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix("https://cinemahistory.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(BuildConfig.APPLICATION_ID).build())
            .buildDynamicLink()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shortLink.uri.toString())

        startActivity(Intent.createChooser(intent, "Share Link"))
    }

    private fun setNextPage() {
        val currentPosition = sumarioContentViewPager.currentItem

        sumarioContentViewPager.setCurrentItem(currentPosition + 1, true)
    }

    fun showFooter() {
        animate(footerContent, 0f, DecelerateInterpolator(2f))
        animate(toolbarNextButton, 0f, DecelerateInterpolator(2f))
        animate(shareButton, 0f, DecelerateInterpolator(2f))
        animate(toolbarImageCardContainer, 0f, DecelerateInterpolator(4f))
    }

    fun hideFooter() {
        animate(footerContent, footerContent.height.toFloat(), AccelerateInterpolator(2f))
        animate(toolbarNextButton, toolbarNextButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(shareButton, shareButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(
            toolbarImageCardContainer,
            toolbarImageCardContainer.height.toFloat() + 10.convertIntToDp(
                this
            ),
            AccelerateInterpolator(2f)
        )
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
        adapterPager = PagePagerAdapter(
            supportFragmentManager,
            lifecycle,
            mainTopic.sumarioList ?: emptyList()
        )

        sumarioContentViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = adapterPager
            currentItem = itemSelectedPosition
        }

        sumarioContentIndicator.attachToViewPager2(sumarioContentViewPager)

        sumarioContentViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                sumarioContentIndicator.onPageSelected(position)
            }
        })
    }


    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"
        const val ITEM_SELECTED_POSITION = "ITEM_SELECTED_POSITION"

        fun newIntent(
            context: Context,
            mainTopic: MainTopicItem,
            itemSelectedPosition: Int
        ): Intent {
            val intent = Intent(context, HistoryPagesActivity::class.java)

            intent.putExtra(ITEM_SELECTED_POSITION, itemSelectedPosition)
            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}