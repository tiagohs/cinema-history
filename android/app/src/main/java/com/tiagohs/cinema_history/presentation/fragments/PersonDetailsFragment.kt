package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.os.Bundle
import android.view.ViewGroup
import com.tiagohs.cinema_history.databinding.FragmentPersonDetailsBinding
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewPersonDepartmentBinding
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

class PersonDetailsFragment: BaseFragment<FragmentPersonDetailsBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPersonDetailsBinding.inflate(inflater, container, false)
    override fun onErrorAction() {}

    lateinit var person: Person

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tablets: cabeçalho e blocos de informação numa coluna centralizada (a foto continua de ponta a ponta).
        binding.headerPersonContainer.limitContentWidth(R.dimen.ls_details_max_width)
        binding.pageContentListContainer.limitContentWidth(R.dimen.ls_details_max_width)

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

        activity?.setupToolbar(binding.toolbar)

        binding.pageContentList.apply {
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

        activity?.startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(context, movieId))
    }

    private fun bindHeader(person: Person) {
        bindPersonProfileImage(person)
        bindPersonDepartments(person)
        bindSocial(person)

        binding.collapsingToolbar.title = person.name
        binding.personName.setResourceText(person.name)
        binding.personBirthInfo.setResourceText(person.birthdayFormated)
    }

    private fun bindPersonProfileImage(person: Person) {
        val profilePath = person.profilePath?.imageUrlFromTMDB(ImageSize.PROFILE_632) ?: return

        binding.personImage.loadImage(
            profilePath,
            getString(R.string.person_photo_description, person.name),
            R.drawable.placeholder_movie_person,
            R.drawable.placeholder_movie_person) {
            binding.personImage.alpha = 1f
            val animation = AnimationUtils.createFadeInAnimation(200) {
                binding.personImageDegrade.alpha = 1f

                AnimationUtils.createPulseAnimation(binding.personName, 1.1f, 1.1f)
                binding.personBirthInfo.startAnimation(AnimationUtils.createFadeInAnimation(200))
            }

            binding.personImage.startAnimation(animation)
        }

    }

    private fun bindPersonDepartments(person: Person) {

        person.departmentsList.forEach {
            binding.jobsScrollView.show()

            val departmentBinding = ViewPersonDepartmentBinding.inflate(LayoutInflater.from(activity), null, false)
            val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0, 0, 10.convertIntToDp(activity), 0)
            departmentBinding.jobName.setResourceText(it)

            departmentBinding.root.layoutParams = layoutParams
            binding.jobsContainer.addView(departmentBinding.root)
        }
    }

    private fun bindSocial(person: Person) {
        val facebookLink = person.externalIds?.facebookId?.let { getString(R.string.facebook_link, it) }
        val twitterLink = person.externalIds?.twitterId?.let { getString(R.string.twitter_link, it) }
        val instagramLink = person.externalIds?.instagramId?.let { getString(R.string.instagram_link, it) }

        bindSocialItem(binding.facebookImageContainer, binding.facebookImage, facebookLink)
        bindSocialItem(binding.twitterImageContainer, binding.twitterImage, twitterLink)
        bindSocialItem(binding.instagramImageContainer, binding.instagramImage, instagramLink)

        if (facebookLink.isNullOrEmpty() && twitterLink.isNullOrEmpty() && instagramLink.isNullOrEmpty()) {
            binding.separatorVertical.setGuidelinePercent(1f)
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
        binding.pageContentListContainer.alpha = 0f
        binding.appBar.alpha = 0f

        (activity as? PersonDetailsActivity)?.startLoading()
    }

    private fun hideLoading() {
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