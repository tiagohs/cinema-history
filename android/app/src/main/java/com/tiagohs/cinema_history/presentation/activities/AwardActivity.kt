package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
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
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_award_details.*
import kotlinx.android.synthetic.main.activity_award_details.appBar
import kotlinx.android.synthetic.main.activity_award_details.collapsingToolbar
import kotlinx.android.synthetic.main.activity_award_details.loadView
import kotlinx.android.synthetic.main.activity_award_details.movieBackdropDegrade
import kotlinx.android.synthetic.main.activity_award_details.movieBackdropDegradeTop
import kotlinx.android.synthetic.main.activity_award_details.pageContentListContainer
import kotlinx.android.synthetic.main.activity_award_details.toolbar
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.view_screen_blocked.*
import javax.inject.Inject

class AwardActivity : BaseActivity(), AwardView {

    @Inject
    lateinit var presenter: AwardPresenter

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    private var awardMainTopic: AwardMainTopic? = null
    private var awardPagerAdapter: AwardPagerAdapter? = null

    override fun onGetLayoutViewId(): Int = R.layout.activity_award_details
    override fun onGetMenuLayoutId(): Int = R.menu.menu_award

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(toolbar)

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

//        dynamicLinkManager.buildMoviePageLink(
//            onComplete = { onBuildPageLinkComplete(it) },
//            onError = { onBuildPageLinkError(it) }
//        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
//        val movieTitle = movie?.getMovieTitleFromAppLanguage(settingManager.getMovieLanguage())
//
//        shareContent(
//            getString(R.string.share_history_page_description, movieTitle, shorLink),
//            getResourceString(
//                R.string.share_title
//            )
//        )

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

        collapsingToolbar.title = awardMainTopic.name
        backdrop.loadImage(
            awardMainTopic.image,
            placeholder = null,
            transform = BlurTransformation(25, 3)
        ) {
            backdrop.alpha = 1f

            AnimationUtils.createShowCircularReveal(backdrop) {
                val animation = AnimationUtils.createFadeInAnimation(150) {
                    movieBackdropDegrade.alpha = 1f
                    movieBackdropDegradeTop.alpha = 1f
                }

                movieBackdropDegrade.startAnimation(animation)
                movieBackdropDegradeTop.startAnimation(animation)

                awardImage.loadImage(awardMainTopic.logo, placeholder = null) {
                    awardImageContainer.alpha = 1f
                    AnimationUtils.createScaleUpAnimation(
                        awardImageContainer,
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
        }

        awardName.setResourceText(awardMainTopic.name)
        awardPresentedBy.setResourceText(awardMainTopic.presentedBy)
        awardCountry.setResourceText(awardMainTopic.country)
    }

    private fun setupSocialLinks() {
        awardMainTopic?.socialList?.forEach { social ->
            when (social.type) {
                SocialType.FACEBOOK -> {
                    facebookImageContainer.show()
                    facebookImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.INSTAGRAM -> {
                    instagramImageContainer.show()
                    instagramImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.SITE -> {
                    siteImageContainer.show()
                    siteImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.TWITTER -> {
                    twitterImageContainer.show()
                    twitterImage.setOnClickListener { openLink(social.link) }
                }
                SocialType.YOUTUBE -> {
                    youtubeImageContainer.show()
                    youtubeImage.setOnClickListener { openLink(social.link) }
                }
            }
        }
    }

    private fun setupTabs() {
        val awardMainTopic = awardMainTopic ?: return

        awardPagerAdapter = AwardPagerAdapter(this, awardMainTopic, supportFragmentManager)

        viewPager.adapter = awardPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }


    private fun showScreenBlocked() {
        screenBlocked.show()
    }

    private fun hideScreenBlocked() {
        screenBlocked.hide()
    }

    override fun startLoading() {
        pageContentListContainer.alpha = 0f
        appBar.alpha = 0f

        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        pageContentListContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        appBar
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadView.hideShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

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