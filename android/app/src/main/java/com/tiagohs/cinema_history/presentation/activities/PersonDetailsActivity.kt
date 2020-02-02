package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.domain.presenter.PersonDetailsPresenter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.fragments.PersonDetailsFragment
import com.tiagohs.cinema_history.presentation.fragments.PersonDetailsSpecialFragment
import com.tiagohs.domain.views.PersonDetailsView
import kotlinx.android.synthetic.main.activity_person_details.*
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
        val isSpecialScreen = person.extraInfo != null
        val fragment = if (isSpecialScreen)
            PersonDetailsSpecialFragment.newInstance(person)
        else
            PersonDetailsFragment.newInstance(person)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun startLoading() {
        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
    }

    override fun hideLoading() {
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

    companion object {

        const val PERSON_ID = "PERSON_ID"

        fun newIntent(context: Context, personId: Int): Intent {
            val intent = Intent(context, PersonDetailsActivity::class.java)

            intent.putExtra(PERSON_ID, personId)

            return intent
        }

    }
}