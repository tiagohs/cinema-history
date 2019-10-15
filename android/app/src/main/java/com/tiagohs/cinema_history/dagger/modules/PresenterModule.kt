package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.cinema_history.presenter.*
import com.tiagohs.cinema_history.services.LocalService
import com.tiagohs.cinema_history.services.TMDBService
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun providerHomePresenter(localService: LocalService): MainTopicsPresenter = MainTopicsPresenterImpl(localService)

    @Provides
    fun providerMilMoviesPresentationPresenter(tmdbService: TMDBService): MilMoviesPresentationPresenter = MilMoviesPresentationPresenterImpl(tmdbService)

    @Provides
    fun providerPresentationPresenter(localService: LocalService): PresentationPresenter = PresentationPresenterImpl(localService)

    @Provides
    fun providerHistoryPagePresenter(localService: LocalService): HistoryPagePresenter = HistoryPagePresenterImpl(localService)
}