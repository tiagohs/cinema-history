package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.TMDBService
import com.tiagohs.cinema_history.ui.views.PersonDetailsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PersonDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService
): BasePresenter<PersonDetailsView>(), PersonDetailsPresenter {

    override fun onBindView(view: PersonDetailsView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchPersonDetails(personId: Int) {
        val appendToResponse = listOf("tagged_images", "images", "movie_credits", "external_ids")

        add(tmdbService.getPersonDetails(personId, appendToResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindPersonDetails(it)
            }, {

            })
        )
    }
}