package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityHistoryPagesBinding
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.ChapterInterstitial
import com.tiagohs.cinema_history.presentation.adapters.PagePagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.tools.SliderTransformer
import com.tiagohs.helpers.tools.ZoomOutPageTransformer
import java.lang.Exception
import javax.inject.Inject


class HistoryPagesActivity : BaseActivity<ActivityHistoryPagesBinding>() {

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    var mainTopic: MainTopicItem? = null
    var adapterPager: PagePagerAdapter? = null

    private var itemSelectedPosition = 0
    private var isFromUniversalLink = false

    var currentPosition: Int = 0

    private val chapterInterstitial by lazy { ChapterInterstitial(this) }

    override fun inflateBinding(inflater: LayoutInflater) = ActivityHistoryPagesBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        startLoading()

        setupArguments()
        setupPagesContainer()
        setupFooter()

        hideLoading()

        chapterInterstitial.preload()
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

    private fun startLoading() {
        binding.pagesContainer.alpha = 0f

        binding.loadView.startShimmer()
        binding.loadView.show()
        binding.loadView.alpha = 1f
    }

    private fun hideLoading() {
        binding.pagesContainer
            ?.animate()
            ?.alpha(1f)
            ?.setDuration(200)
            ?.setInterpolator(DecelerateInterpolator(2f))
            ?.start()

        binding.loadView
            ?.animate()
            ?.alpha(0f)
            ?.setDuration(150)
            ?.setInterpolator(AccelerateInterpolator(2f))
            ?.setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    binding.loadView?.hideShimmer()
                    binding.loadView?.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {}

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

        binding.toolbarImage.loadImage(image)
        binding.toolbarImageCardContainer.setOnClickListener {
            onBackPressed()
        }

        binding.toolbarNextButton.setOnClickListener {
            // Intersticial só nesta ação explícita (nunca no swipe), e o capítulo muda depois de fechá-lo.
            chapterInterstitial.onNextChapterRequested { setNextPage() }
        }

        binding.shareButton.setOnClickListener { onShareClicked() }
    }

    private fun onShareClicked() {
        val mainTopicId = mainTopic?.id ?: return

        showScreenBlocked()

        dynamicLinkManager.buildHistoryCinemaPageLink(
            mainTopicId,
            currentPosition,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
        val sumarioItemTitle = mainTopic?.sumarioList?.get(currentPosition)?.title

        shareContent(getString(R.string.share_history_page_description, sumarioItemTitle,shorLink), getResourceString(R.string.share_title))

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    private fun setNextPage() {
        val currentPosition = binding.sumarioContentViewPager.currentItem

        binding.sumarioContentViewPager.setCurrentItem(currentPosition + 1, true)
    }

    fun showScreenBlocked() {
        binding.screenBlocked.root.show()
    }

    fun hideScreenBlocked() {
        binding.screenBlocked.root.hide()
    }

    fun showFooter() {
        animate(binding.footerContent, 0f, DecelerateInterpolator(2f))
        animate(binding.toolbarNextButton, 0f, DecelerateInterpolator(2f))
        animate(binding.shareButton, 0f, DecelerateInterpolator(2f))
        animate(binding.sumarioContentIndicatorContainer, 0f, DecelerateInterpolator(2f))
        animate(binding.toolbarImageCardContainer, 0f, DecelerateInterpolator(4f))
    }

    fun hideFooter() {
        animate(binding.footerContent, binding.footerContent.height.toFloat(), AccelerateInterpolator(2f))
        animate(binding.toolbarNextButton, binding.toolbarNextButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(binding.shareButton, binding.shareButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(binding.sumarioContentIndicatorContainer, binding.shareButton.height.toFloat(), AccelerateInterpolator(2f))
        animate(
            binding.toolbarImageCardContainer,
            binding.toolbarImageCardContainer.height.toFloat() + 10.convertIntToDp(
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
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)
    }

    private fun setupPagesContainer() {
        val mainTopic = this.mainTopic ?: return
        adapterPager = PagePagerAdapter(
            supportFragmentManager,
            lifecycle,
            mainTopic.sumarioList ?: emptyList()
        )

        setupNextButtonBlocked(itemSelectedPosition)

        binding.sumarioContentViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = adapterPager
            currentItem = itemSelectedPosition

            setPageTransformer(ZoomOutPageTransformer())

            binding.sumarioContentIndicator.attachToViewPager2(this)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPosition = position

                    binding.sumarioContentIndicator.onPageSelected(position)

                    setupNextButtonBlocked(currentPosition)
                }
            })
        }
    }

    private fun setupNextButtonBlocked(currentPosition: Int) {
        val numberOfItens = mainTopic?.sumarioList?.size ?: 0

        if (currentPosition == numberOfItens - 1) {
            binding.toolbarNextButton.alpha = 0.4f
            binding.toolbarNextButton.isClickable = false
            return
        }

        binding.toolbarNextButton.alpha = 1f
        binding.toolbarNextButton.isClickable = true
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"
        const val ITEM_SELECTED_POSITION = "ITEM_SELECTED_POSITION"

        fun newIntent(
            context: Context,
            mainTopic: MainTopicItem,
            itemSelectedPosition: Int,
            isFromUniversalLink: Boolean = false
        ): Intent {
            val intent = Intent(context, HistoryPagesActivity::class.java)

            intent.putExtra(ITEM_SELECTED_POSITION, itemSelectedPosition)
            intent.putExtra(MAIN_TOPIC, mainTopic)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }
    }
}