package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityAwardDetailsBinding
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.AwardPagerAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.presenter.AwardPresenter
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.enums.SocialType
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import javax.inject.Inject

class AwardActivity : BaseActivity<ActivityAwardDetailsBinding>(), AwardView {

    @Inject
    lateinit var presenter: AwardPresenter

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    private var awardMainTopic: AwardMainTopic? = null
    private var awardPagerAdapter: AwardPagerAdapter? = null

    override fun inflateBinding(inflater: LayoutInflater) = ActivityAwardDetailsBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = R.menu.menu_award

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(binding.toolbar)

        presenter.onBindView(this)
        presenter.fetchAwardsNominees(awardMainTopic)
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
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
        val id = awardMainTopic?.id ?: return

        dynamicLinkManager.buildAwardPageLink(
            id,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
        shareContent(
            getString(R.string.share_history_page_description, awardMainTopic?.name, shorLink),
            getResourceString(
                R.string.share_title
            )
        )

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    override fun setupArguments() {
        awardMainTopic = intent.extras?.getSerializable(MAIN_TOPIC) as? AwardMainTopic
    }

    override fun bindAwardsNomineesContent(awardMainTopic: AwardMainTopic) {
        this.awardMainTopic = awardMainTopic

        setupHeader()
        setupSocialLinks()
        setupTabs()
    }

    private fun setupHeader() {
        val awardMainTopic = awardMainTopic ?: return

        binding.collapsingToolbar.title = awardMainTopic.name

        awardMainTopic.image.imageStyle = ImageStyle(
            height = 150,
            resize = ImageResize(
                height = 150
            )
        )
        binding.backdrop.loadImage(
            awardMainTopic.image,
            placeholder = null,
            transform = BlurTransformation(25, 3)) {
            awardMainTopic.logo.imageStyle = ImageStyle(
                height = 80,
                resize = ImageResize(
                    width = 80,
                    height = 80
                )
            )
            binding.awardImage.loadImage(awardMainTopic.logo, placeholder = null) {
                binding.awardImageContainer.alpha = 1f
                AnimationUtils.createScaleUpAnimation(
                    binding.awardImageContainer,
                    0f,
                    1f,
                    0f,
                    1f,
                    0.5f,
                    0.5f,
                    200,
                    150
                )
            }
        }

        binding.awardName.setResourceText(awardMainTopic.name)
        binding.awardPresentedBy.setResourceText(awardMainTopic.presentedBy)
        binding.awardCountry.setResourceText(awardMainTopic.country)
    }

    private fun setupSocialLinks() {
        awardMainTopic?.socialList?.forEach { social ->
            when (social.type) {
                SocialType.FACEBOOK -> {
                    binding.facebookImageContainer.show()
                    binding.facebookImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.INSTAGRAM -> {
                    binding.instagramImageContainer.show()
                    binding.instagramImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.SITE -> {
                    binding.siteImageContainer.show()
                    binding.siteImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.TWITTER -> {
                    binding.twitterImageContainer.show()
                    binding.twitterImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.YOUTUBE -> {
                    binding.youtubeImageContainer.show()
                    binding.youtubeImage.setOnClickListener { openLink(social.link) }
                }
            }
        }
    }

    private fun setupTabs() {
        val awardMainTopic = awardMainTopic ?: return

        awardPagerAdapter = AwardPagerAdapter(this, awardMainTopic, supportFragmentManager)

        binding.viewPager.adapter = awardPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }


    private fun showScreenBlocked() {
        binding.screenBlocked.root.show()
    }

    private fun hideScreenBlocked() {
        binding.screenBlocked.root.hide()
    }

    override fun startLoading() {
        binding.pageContentListContainer.alpha = 0f
        binding.appBar.alpha = 0f

        binding.loadView.showShimmer(true)
        binding.loadView.show()
    }

    override fun hideLoading() {
        binding.pageContentListContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.appBar
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    binding.loadView.hideShimmer()
                    binding.loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {}

            })
            .start()
    }


    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newIntent(mainTopic: AwardMainTopic, context: Context): Intent {
            val intent = Intent(context, AwardActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}