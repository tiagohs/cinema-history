package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.views.MilMoviesPresentationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MilMoviesPresentationPresenterImpl @Inject constructor(
    val tmdbService: TMDBService
): MilMoviesPresentationPresenter, BasePresenter<MilMoviesPresentationView>() {

    var page = 0
    var totalPage = 0

    override fun onBindView(view: MilMoviesPresentationView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchMoviesByListId(listId: String) {
        view?.startLoading()

        add(tmdbService.getList(listId, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                totalPage = it.total_pages
                val movies = it.results

                view?.hideLoading()
                view?.bindMovieList(movies)
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
                view?.hideLoading()
            })
        )
    }

    override fun fetchMoreMovies(listId: String, page: Int) {
        this.page = page

        add(tmdbService.getList(listId, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val movies = it.results

                view?.bindMoreMovies(movies)
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
            })
        )
    }

    override fun hasMorePages(): Boolean = page < totalPage
}