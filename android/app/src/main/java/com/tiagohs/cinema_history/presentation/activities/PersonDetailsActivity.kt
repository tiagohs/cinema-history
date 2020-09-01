package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.domain.presenter.PersonDetailsPresenter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.fragments.PersonDetailsFragment
import com.tiagohs.cinema_history.presentation.fragments.PersonDetailsSpecialFragment
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.domain.views.PersonDetailsView
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.activity_person_details.*
import kotlinx.android.synthetic.main.view_screen_blocked.*
import java.lang.Exception
import javax.inject.Inject

class PersonDetailsActivity: BaseActivity(), PersonDetailsView {

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    override fun onGetLayoutViewId(): Int = R.layout.activity_person_details
    override fun onGetMenuLayoutId(): Int = R.menu.menu_person

    @Inject
    lateinit var presenter: PersonDetailsPresenter

    @Inject
    lateinit var settingManager: SettingsManager

    var personId: Int = 0
    var person: Person? = null
    private var isFromUniversalLink = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchPersonDetails(personId, settingManager.getMovieISOLanguage())
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

        dynamicLinkManager.buildPersonPageLink(
            personId,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
        val personName = person?.name

        shareContent(getString(R.string.share_history_page_description, personName, shorLink), getResourceString(R.string.share_title))

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

    override fun setupArguments() {
        personId = intent.getIntExtra(PERSON_ID, 0)
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
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

    override fun bindPersonDetails(person: Person) {
        this.person = person

        val isSpecialScreen = person.extraInfo != null
        val fragment = if (isSpecialScreen)
            PersonDetailsSpecialFragment.newInstance(person)
        else
            PersonDetailsFragment.newInstance(person)

        startFragment(R.id.container, fragment)
    }

    override fun startLoading() {
        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadView?.hideShimmer()
                    loadView?.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    companion object {

        const val PERSON_ID = "PERSON_ID"

        fun newIntent(context: Context, personId: Int, isFromUniversalLink: Boolean = false): Intent {
            val intent = Intent(context, PersonDetailsActivity::class.java)

            intent.putExtra(PERSON_ID, personId)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }

    }
}