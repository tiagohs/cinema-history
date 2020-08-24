package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.person_info.PersonInfoMovieList
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.cinema_history.presentation.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.presentation.activities.PersonDetailsActivity
import com.tiagohs.cinema_history.presentation.adapters.PersonInfoAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.PersonInfoType
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.fragment_person_details.*
import kotlinx.android.synthetic.main.view_person_department.view.*

class PersonDetailsFragment: BaseFragment() {

    override fun getViewID(): Int = R.layout.fragment_person_details
    override fun onErrorAction() {}

    lateinit var person: Person

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArguments()
        bindPersonDetails()
    }

    private fun setupArguments() {
        val arguments = arguments ?: return

        person = arguments.getSerializable(PERSON) as Person
    }

    private fun bindPersonDetails() {
        val activity = (activity as? PersonDetailsActivity)
        val personInfoContentList = generatePersonInfoList(person)

        activity?.setupToolbar(toolbar)

        pageContentList.apply {
            adapter = PersonInfoAdapter(personInfoContentList, false).apply {
                onMovieSelected = { onMovieSelected(it) }
                onLinkClick = { activity?.openLink(it) }
                onVideoClick = { videoId ->
                    activity?.openLink(
                        getString(R.string.youtube_link, videoId)
                    )
                }
            }
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }

        bindHeader(person)

        hideLoading()
    }

    private fun onMovieSelected(movieId: Int) {
        val context = context ?: return

        startActivity(MovieDetailsActivity.newIntent(context, movieId))
    }

    private fun bindHeader(person: Person) {
        bindPersonProfileImage(person)
        bindPersonDepartments(person)
        bindSocial(person)

        collapsingToolbar.title = person.name
        personName.setResourceText(person.name)
        personBirthInfo.setResourceText(person.birthdayFormated)
    }

    private fun bindPersonProfileImage(person: Person) {
        val profilePath = person.profilePath?.imageUrlFromTMDB(ImageSize.PROFILE_632) ?: return

        personImage.loadImage(
            profilePath,
            getString(R.string.person_photo_description, person.name),
            R.drawable.placeholder_movie_person,
            R.drawable.placeholder_movie_person) {
            personImage.alpha = 1f
            val animation = AnimationUtils.createFadeInAnimation(200) {
                personImageDegrade.alpha = 1f

                AnimationUtils.createPulseAnimation(personName, 1.1f, 1.1f)
                personBirthInfo.startAnimation(AnimationUtils.createFadeInAnimation(200))
            }

            personImage.startAnimation(animation)
        }

    }

    private fun bindPersonDepartments(person: Person) {

        person.departmentsList.forEach {
            jobsScrollView.show()

            val view = LayoutInflater.from(activity).inflate(R.layout.view_person_department, null, false)
            val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0, 0, 10.convertIntToDp(activity), 0)
            view.jobName.setResourceText(it)

            view.layoutParams = layoutParams
            jobsContainer.addView(view)
        }
    }

    private fun bindSocial(person: Person) {
        val facebookLink = person.externalIds?.facebookId?.let { getString(R.string.facebook_link, it) }
        val twitterLink = person.externalIds?.twitterId?.let { getString(R.string.twitter_link, it) }
        val instagramLink = person.externalIds?.instagramId?.let { getString(R.string.instagram_link, it) }

        bindSocialItem(facebookImageContainer, facebookImage, facebookLink)
        bindSocialItem(twitterImageContainer, twitterImage, twitterLink)
        bindSocialItem(instagramImageContainer, instagramImage, instagramLink)

        if (facebookLink.isNullOrEmpty() && twitterLink.isNullOrEmpty() && instagramLink.isNullOrEmpty()) {
            separatorVertical.setGuidelinePercent(1f)
        }
    }

    private fun bindSocialItem(imageContainer: ConstraintLayout, image: ImageView, socialPath: String?) {

        socialPath?.let { url ->
            imageContainer.show()

            image.setOnClickListener {
                activity?.openLink(url)
            }
        }

    }

    private fun generatePersonInfoList(person: Person): List<PersonInfo> {
        val listOfPersonInfo = ArrayList<PersonInfo>()

        if (!person.biography.isNullOrBlank()) {
            listOfPersonInfo.add(PersonInfo(PersonInfoType.INFO_BIOGRAPHY, person))
        }

        if (person.personFilmography.isNotEmpty()) {
            listOfPersonInfo.add(PersonInfoMovieList(PersonInfoType.INFO_FILMOGRAPHY, person, person.personFilmography, "Filmography"))
        }

        if (person.allImages.isNotEmpty()) {
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

        fun newInstance(person: Person): PersonDetailsFragment {
            val personDetailsFragment = PersonDetailsFragment()
            personDetailsFragment.arguments = Bundle().apply {
                putSerializable(PERSON, person)
            }

            return personDetailsFragment
        }
    }
}