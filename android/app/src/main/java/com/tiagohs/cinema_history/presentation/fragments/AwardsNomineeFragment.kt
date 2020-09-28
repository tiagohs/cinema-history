package com.tiagohs.cinema_history.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.presentation.activities.PersonDetailsActivity
import com.tiagohs.cinema_history.presentation.adapters.PageContentAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.AwardsPageType
import com.tiagohs.entities.enums.NomineeType
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.fragment_awards_nominees_content.pageContentList
import kotlinx.android.synthetic.main.fragment_awards_nominees_content.spinner
import javax.inject.Inject

class AwardsNomineeFragment : BaseFragment() {

    @Inject
    lateinit var settingManager: SettingsManager

    override fun getViewID(): Int = R.layout.fragment_awards_nominees_content

    private var awardMainTopic: AwardMainTopic? = null
    private var awardsPageType: AwardsPageType? = null

    private var isListSetup = false

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
        val contentList = awardMainTopic?.nomineesList?.firstOrNull()?.content ?: emptyList()
        val typesSpinnerList = awardMainTopic?.nomineesList?.map { it.year } ?: return
        val context = activity ?: return

        spinner.adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, typesSpinnerList)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val contents = awardMainTopic?.nomineesList?.getOrNull(position)?.content ?: return

                setupReviewList(contents)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setupReviewList(contentList)
    }

    private fun setupReviewList(contentList: List<Content>) {
        if (!isListSetup) {
            pageContentList.addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.TOP
                )
            )

            isListSetup = true
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
        ): AwardsNomineeFragment {
            val awardsFragment = AwardsNomineeFragment()
            awardsFragment.arguments = Bundle().apply {
                putSerializable(AWARD_MAIN_TOPIC, awardMainTopic)
                putSerializable(AWARD_PAGE_TYPE, awardsPageType)
            }

            return awardsFragment
        }
    }
}