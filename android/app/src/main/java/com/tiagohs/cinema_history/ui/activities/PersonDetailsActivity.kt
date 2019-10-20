package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.presenter.PersonDetailsPresenter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.PersonDetailsView
import javax.inject.Inject

class PersonDetailsActivity: BaseActivity(), PersonDetailsView {

    override fun onGetLayoutViewId(): Int = R.layout.acitivy_person_details
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

    override fun bindPersonDetails(person: Person) {

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