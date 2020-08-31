package com.tiagohs.cinema_history.presentation.activities

import android.app.TaskStackBuilder
import android.net.Uri
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.presenter.UniversalLinkPresenter
import com.tiagohs.domain.views.UniversalLinkView
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.enums.ShareScreenTypeEnum
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import kotlinx.android.synthetic.main.activity_universal_link.*
import java.lang.Exception
import javax.inject.Inject

class UniversalLinkActivity: BaseActivity(), UniversalLinkView {

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    @Inject
    lateinit var presenter: UniversalLinkPresenter

    override fun onGetLayoutViewId(): Int = R.layout.activity_universal_link
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
    }

    override fun onBackPressed() {
        startHomeActivity()
    }

    override fun setupContentFromUniversalLink() {
        dynamicLinkManager.findScreenFromLink(this,
            onComplete = { screenType, deepLink -> onFindScreenComplete(screenType, deepLink) },
            onError = { onFindScreenError(it) }
        )
    }

    private fun onFindScreenComplete(screenType: ShareScreenTypeEnum, deepLink: Uri) {
        when (screenType) {
            ShareScreenTypeEnum.HISTORY_PAGE -> {
                val mainTopicID = deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.MAIN_TOPIC_ID)
                val pagePosition = deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.HISTORY_PAGE_POSITION)

                presenter.fetchMainTopicById(mainTopicID, pagePosition)
            }
            ShareScreenTypeEnum.MOVIE_PAGE -> {
                val movieId = deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.MOVIE_ID)

                if (movieId != null) {
                    startMovieDetails(movieId)
                    return
                }

                startHomeActivity()
            }

            ShareScreenTypeEnum.PERSON_PAGE -> {
                val personId = deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.PERSON_ID)

                if (personId != null) {
                    startPersonDetails(personId)
                    return
                }

                startHomeActivity()
            }
            ShareScreenTypeEnum.TIMELINE_PAGE -> {
                val timelinePosition = deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.TIMELINE_INDEX)

                startTimelineScreen(timelinePosition ?: "0")
            }
            else -> { startHomeActivity() }
        }
    }

    private fun onFindScreenError(error: Exception) {
        startHomeActivity()
    }

    override fun startTimelineScreen(timelinePosition: String) {
        startActivityWithSlideRightToLeftAnimation(TimelineActivity.newIntent(this, startIndex = timelinePosition.toInt(), isFromUniversalLink = true))
        finish()
    }

    override fun startMovieDetails(movieId: String) {
        startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(this, movieId = movieId.toInt(), isFromUniversalLink = true))
        finish()
    }

    override fun startPersonDetails(personId: String) {
        startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(this, personId = personId.toInt(), isFromUniversalLink = true))
        finish()
    }

    override fun startHistoryPage(mainTopic: MainTopicItem, itemSelectedPosition: Int) {
        TaskStackBuilder.create(this).apply {
            addParentStack(HomeActivity::class.java)
            addNextIntent(HistoryPagesActivity.newIntent(this@UniversalLinkActivity, mainTopic, itemSelectedPosition, isFromUniversalLink = true))

            startActivities()
        }

        finish()
    }

    override fun startHomeActivity() {
        startActivity(HomeActivity.newIntent(this))
        finish()
    }
}