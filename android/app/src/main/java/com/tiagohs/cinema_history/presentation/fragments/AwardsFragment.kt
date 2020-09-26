package com.tiagohs.cinema_history.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.presentation.activities.PersonDetailsActivity
import com.tiagohs.cinema_history.presentation.adapters.PageContentAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.enums.AwardsPageType
import com.tiagohs.entities.enums.NomineeType
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.fragment_history_page.*
import javax.inject.Inject

class AwardsFragment : BaseFragment() {

    @Inject
    lateinit var settingManager: SettingsManager

    override fun getViewID(): Int = R.layout.fragment_awards_content

    private var awardMainTopic: AwardMainTopic? = null
    private var awardsPageType: AwardsPageType? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getApplicationComponent()?.inject(this)

        setupArguments()
        setupPageContent()
    }

    private fun setupArguments() {
        awardMainTopic = arguments?.getSerializable(AWARD_MAIN_TOPIC) as? AwardMainTopic
        awardsPageType = arguments?.getSerializable(AWARD_PAGE_TYPE) as? AwardsPageType
    }

    private fun setupPageContent() {
        val contentList = when (awardsPageType) {
            AwardsPageType.HISTORY -> awardMainTopic?.history ?: emptyList()
            else -> emptyList()
        }

        pageContentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter =
                PageContentAdapter(contentList, null, settingManager.getMovieLanguage()).apply {
                    presentScreen = { presentScreen(it) }
                    onMovieClicked = { onMovieSelected(it) }
                    onPersonClicked = { onPersonClicked(it) }
                    onNomineeClicked = { onNomineeClicked(it) }
                }
            addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.TOP
                )
            )
        }
    }

    private fun presentScreen(intent: Intent) {
        activity?.startActivityWithSlideRightToLeftAnimation(intent)
    }

    private fun onMovieSelected(movieId: Int) {
        val context = context ?: return

        activity?.startActivityWithSlideRightToLeftAnimation(
            MovieDetailsActivity.newIntent(
                context,
                movieId
            )
        )
    }

    private fun onPersonClicked(personId: Int) {
        val context = context ?: return

        activity?.startActivityWithSlideRightToLeftAnimation(
            PersonDetailsActivity.newIntent(
                context,
                personId
            )
        )
    }

    private fun onNomineeClicked(nominee: Nominee) {
        val id = nominee.id ?: return

        when (nominee.type) {
            NomineeType.MOVIE -> onMovieSelected(id)
            NomineeType.PERSON -> onPersonClicked(id)
        }
    }

    override fun onErrorAction() {

    }

    companion object {

        const val AWARD_MAIN_TOPIC = "AWARD_MAIN_TOPIC"
        const val AWARD_PAGE_TYPE = "AWARD_PAGE_TYPE"

        fun newInstance(
            awardMainTopic: AwardMainTopic,
            awardsPageType: AwardsPageType
        ): AwardsFragment {
            val awardsFragment = AwardsFragment()
            awardsFragment.arguments = Bundle().apply {
                putSerializable(AWARD_MAIN_TOPIC, awardMainTopic)
                putSerializable(AWARD_PAGE_TYPE, awardsPageType)
            }

            return awardsFragment
        }
    }
}