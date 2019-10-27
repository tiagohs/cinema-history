package com.tiagohs.cinema_history.ui.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.enums.PersonInfoType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.models.movie_info.PersonInfo
import com.tiagohs.cinema_history.models.movie_info.PersonInfoMovieList
import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.presenter.PersonDetailsPresenter
import com.tiagohs.cinema_history.ui.adapters.PersonInfoAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.PersonDetailsView
import kotlinx.android.synthetic.main.activity_person_details.*
import kotlinx.android.synthetic.main.view_person_department.view.*
import javax.inject.Inject

class PersonDetailsActivity: BaseActivity(), PersonDetailsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_person_details
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: PersonDetailsPresenter

    var personId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchPersonDetails(personId)
    }

    override fun setupArguments() {
        personId = intent.getIntExtra(PERSON_ID, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun bindPersonDetails(person: Person) {
        val personInfoContentList = generatePersonInfoList(person)
        val adapter = PersonInfoAdapter(this, personInfoContentList)

        adapter.onMovieSelected = { onMovieSelected(it) }

        pageContentList.adapter = adapter
        pageContentList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bindHeader(person)
    }

    override fun startLoading() {
        pageContentListContainer.alpha = 0f
        appBar.alpha = 0f

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
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
                    loadView.stopShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun onMovieSelected(movieId: Int) {
        startActivity(MovieDetailsActivity.newIntent(this, movieId))
    }

    private fun bindHeader(person: Person) {

        bindPersonProfileImage(person)
        bindPersonDepartments(person)
        bindSocial(person)

        collapsingToolbar.title = person.name
        personName.text = person.name
        personBirthInfo.text = getBirthIfo(person)
    }

    private fun bindPersonProfileImage(person: Person) {
        val profilePath = person.profilePath?.imageUrlFromTMDB(ImageSize.PROFILE_632) ?: return

        personImage.loadImage(profilePath, R.drawable.placeholder_movie_person, R.drawable.placeholder_movie_person) {
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
            jobsScrollView.visibility = View.VISIBLE

            val view = LayoutInflater.from(this).inflate(R.layout.view_person_department, null, false)
            val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0, 0, 10.convertIntToDp(this), 0)
            view.jobName.text = it

            view.layoutParams = layoutParams
            jobsContainer.addView(view)
        }
    }

    private fun bindSocial(person: Person) {
        val facebookLink = person.externalIds?.facebookId?.let { "https://www.facebook.com/$it" }
        val twitterLink = person.externalIds?.facebookId?.let { "https://twitter.com/$it" }
        val instagramLink = person.externalIds?.facebookId?.let { "https://instagram.com/$it" }

        bindSocialItem(facebookImageContainer, facebookImage, facebookLink)
        bindSocialItem(twitterImageContainer, twitterImage, twitterLink)
        bindSocialItem(instagramImageContainer, instagramImage, instagramLink)

        if (facebookLink.isNullOrEmpty() && twitterLink.isNullOrEmpty() && instagramLink.isNullOrEmpty()) {
            separatorVertical.setGuidelinePercent(1f)
        }
    }

    private fun bindSocialItem(imageContainer: ConstraintLayout, image: ImageView, socialPath: String?) {

        socialPath?.let { url ->
            imageContainer.visibility = View.VISIBLE

            image.setOnClickListener {
                openUrl(url)
            }
        }

    }

    private fun generatePersonInfoList(person: Person): List<PersonInfo> = listOf(
        PersonInfo(PersonInfoType.INFO_BIOGRAPHY, person),
        PersonInfoMovieList(PersonInfoType.INFO_FILMOGRAPHY, person, person.personFilmography, "Filmography")
    )

    private fun getBirthIfo(person: Person): String {
        val birthdayDate = person.birthday
        val placeOfBirth = person.placeOfBirth

        if (birthdayDate.isNullOrEmpty() && placeOfBirth.isNullOrEmpty()) { return "" }

        if (placeOfBirth.isNullOrEmpty() && !birthdayDate.isNullOrEmpty()) {
            return DateUtils.formateDate(birthdayDate, "MMMM dd, yyyy")
        }

        if (!placeOfBirth.isNullOrEmpty() && birthdayDate.isNullOrEmpty()) {
            return placeOfBirth
        }

        return "${DateUtils.formateDate(birthdayDate!!, "MMMM dd, yyyy")} in $placeOfBirth"
    }

    companion object {

        const val PERSON_ID = "PERSON_ID"

        fun newIntent(context: Context, personId: Int): Intent {
            val intent = Intent(context, PersonDetailsActivity::class.java)

            intent.putExtra(PERSON_ID, personId)

            return intent
        }

    }
}