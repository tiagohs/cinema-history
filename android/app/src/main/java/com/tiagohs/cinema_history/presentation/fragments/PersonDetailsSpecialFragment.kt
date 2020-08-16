package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.cinema_history.presentation.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.presentation.activities.PersonDetailsActivity
import com.tiagohs.cinema_history.presentation.adapters.PersonInfoAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.enums.PersonInfoType
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.fragment_person_details_special.*

class PersonDetailsSpecialFragment: BaseFragment() {

    override fun getViewID(): Int = R.layout.fragment_person_details_special
    override fun onErrorAction() {}

    lateinit var person: Person

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArguments()
        bindPersonDetails()
    }

    private fun setupArguments() {
        val arguments = arguments ?: return

        person = arguments.getSerializable(PersonDetailsFragment.PERSON) as Person
    }

    private fun bindPersonDetails() {
        val activity = (activity as? PersonDetailsActivity)

        activity?.setupToolbar(toolbar)

        bindHeader()
        bindContentList()

        hideLoading()
    }

    private fun bindHeader() {
        val personExtraInfo = person.extraInfo ?: return

        personName.setupLinkableTextView(context)
        personQuote.setupLinkableTextView(context)

        personName.setResourceStyledText(personExtraInfo.customName)
        personQuote.setResourceStyledText(personExtraInfo.quote)
        moviesQuantity.setResourceText(person.personFilmography.size.toString())

        bindPersonImage(personExtraInfo.highlight_image)
    }

    private fun bindContentList() {
        val activity = (activity as? PersonDetailsActivity)
        val personInfoContentList = generatePersonInfoList(person)
        val adapter = PersonInfoAdapter(personInfoContentList, true).apply {
            onMovieSelected = { onMovieSelected(it) }
            onLinkClick = { activity?.openLink(it) }
            onVideoClick = { videoId ->
                activity?.openLink(
                    getString(R.string.youtube_link, videoId)
                )
            }
        }

        pageContentList.adapter = adapter
        pageContentList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private fun bindPersonImage(imageName: String?) {
        val imageUrl = imageName ?: return
        val image = Image(ImageType.LOCAL, imageUrl)

        personImage.loadImageBlackAndWhite(image, null)
    }

    private fun onMovieSelected(movieId: Int) {
        val context = context ?: return

        startActivity(MovieDetailsActivity.newIntent(context, movieId))
    }

    private fun generatePersonInfoList(person: Person): List<PersonInfo> {
        val listOfPersonInfo = ArrayList<PersonInfo>()

        if (!person.biography.isNullOrBlank()) {
            listOfPersonInfo.add(PersonInfo(PersonInfoType.INFO_SPECIAL_BIOGRAPHY, person))
        }

        val personExtraInfo = person.extraInfo?: return listOfPersonInfo

        if (!personExtraInfo.profile.isNullOrEmpty()) {
            listOfPersonInfo.add(PersonInfo(PersonInfoType.INFO_SPECIAL_PROFILE, person))
        }

        if (!person.personFilmography.isNullOrEmpty()) {
            listOfPersonInfo.add(PersonInfo(PersonInfoType.INFO_SPECIAL_FILMOGRAPHY, person))
        }

        if (!person.allImages.isNullOrEmpty() || !personExtraInfo.videos.isNullOrEmpty()) {
            listOfPersonInfo.add(PersonInfo(PersonInfoType.INFO_MIDIA, person))
        }

        return listOfPersonInfo
    }

    private fun startLoading() {
        pageContentListContainer.alpha = 0f
        appBar.alpha = 0f

        (activity as? PersonDetailsActivity)?.startLoading()
    }

    private fun hideLoading() {
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

        (activity as? PersonDetailsActivity)?.hideLoading()
    }


    companion object {

        const val PERSON = "PERSON"

        fun newInstance(person: Person): PersonDetailsSpecialFragment {
            val personDetailsSpecialFragment = PersonDetailsSpecialFragment()
            personDetailsSpecialFragment.arguments = Bundle().apply {
                putSerializable(PERSON, person)
            }

            return personDetailsSpecialFragment
        }
    }
}