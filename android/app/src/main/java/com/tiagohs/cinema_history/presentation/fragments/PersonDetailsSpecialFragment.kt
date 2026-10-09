package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.FragmentPersonDetailsSpecialBinding
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

class PersonDetailsSpecialFragment: BaseFragment<FragmentPersonDetailsSpecialBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPersonDetailsSpecialBinding.inflate(inflater, container, false)
    override fun onErrorAction() {}

    lateinit var person: Person

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tablets: cabeçalho e blocos de informação numa coluna centralizada (a foto continua de ponta a ponta).
        binding.headerSpecialContainer.limitContentWidth(R.dimen.ls_details_max_width)
        binding.pageContentListContainer.limitContentWidth(R.dimen.ls_details_max_width)

        setupArguments()
        bindPersonDetails()
    }

    private fun setupArguments() {
        val arguments = arguments ?: return

        person = arguments.getSerializable(PersonDetailsFragment.PERSON) as Person
    }

    private fun bindPersonDetails() {
        val activity = (activity as? PersonDetailsActivity)

        activity?.setupToolbar(binding.toolbar)

        bindHeader()
        bindContentList()

        hideLoading()
    }

    private fun bindHeader() {
        val personExtraInfo = person.extraInfo ?: return

        binding.personName.setupLinkableTextView(context)
        binding.personQuote.setupLinkableTextView(context)

        binding.personName.setResourceStyledText(personExtraInfo.customName)
        binding.personQuote.setResourceStyledText(personExtraInfo.quote)
        binding.moviesQuantity.setResourceText(person.personFilmography.size.toString())

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

        binding.pageContentList.adapter = adapter
        binding.pageContentList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private fun bindPersonImage(imageName: String?) {
        val imageUrl = imageName ?: return
        val image = Image(ImageType.LOCAL, imageUrl)

        binding.personImage.loadImageBlackAndWhite(image, null)
    }

    private fun onMovieSelected(movieId: Int) {
        val context = context ?: return

        activity?.startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(context, movieId))
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

        fun newInstance(person: Person): PersonDetailsSpecialFragment {
            val personDetailsSpecialFragment = PersonDetailsSpecialFragment()
            personDetailsSpecialFragment.arguments = Bundle().apply {
                putSerializable(PERSON, person)
            }

            return personDetailsSpecialFragment
        }
    }
}