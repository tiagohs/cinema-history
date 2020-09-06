package com.tiagohs.domain.presenter

import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.views.PersonDetailsView
import com.tiagohs.helpers.R
import com.tiagohs.helpers.utils.MovieUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PersonDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService,
    val localService: LocalService
): BasePresenter<PersonDetailsView>(), PersonDetailsPresenter {

    override fun onBindView(view: PersonDetailsView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchPersonDetails(personId: Int, languageToUse: String) {
        val appendToResponse =
            listOf("tagged_images", "images", "movie_credits", "external_ids", "translations")

        view?.startLoading()

        add(tmdbService.getPersonDetails(personId, languageToUse, appendToResponse)
            .map { setupPersonDetails(it) }
            .concatMap { fetchExtraInfo(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindPersonDetails(it)
            }, {
                view?.onError(it, R.string.error_unknown)
                view?.hideLoading()
            })
        )
    }

    private fun setupPersonDetails(person: Person): Person {
        MovieUtils.setupBirthdayInfo(person)

        person.generatePersonDepartmentsList()
        person.setupPersonSummmary()
        person.setupPersonImages()

        person.personFilmography = MovieUtils.generatePersonMovieCredits(person.movieCredits)

        return person
    }

    private fun fetchExtraInfo(person: Person): Observable<Person> =
        localService.getSpecialPersons()
            .map { personExtraInfoList ->
                personExtraInfoList.find { personExtra ->
                    personExtra.id == person.id
                }?.let {
                    person.extraInfo = it
                }

                person
            }
            .onErrorResumeNext { _: Throwable -> return@onErrorResumeNext Observable.just(person) }
}