package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.TMDBService
import com.tiagohs.cinema_history.ui.views.MilMoviesPresentationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MilMoviesPresentationPresenterImpl @Inject constructor(
    val tmdbService: TMDBService
): MilMoviesPresentationPresenter, BasePresenter<MilMoviesPresentationView>() {

    override fun onBindView(view: MilMoviesPresentationView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchMoviesByListId(listId: String) {

        add(tmdbService.getList(listId, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val movies = it.results

                view?.bindMovieList(movies)
            }, {

            })
        )
    }
}