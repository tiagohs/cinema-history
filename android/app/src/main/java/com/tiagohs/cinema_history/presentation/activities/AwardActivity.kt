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
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.presenter.AwardPresenter
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.activity_award_details.*
import kotlinx.android.synthetic.main.view_screen_blocked.*
import javax.inject.Inject

class AwardActivity : BaseActivity(), AwardView {

    @Inject
    lateinit var presenter: AwardPresenter

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    override fun onGetLayoutViewId(): Int = R.layout.activity_award_details
    override fun onGetMenuLayoutId(): Int = R.menu.menu_award

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
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