package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.domain.presenter.*
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.services.OMDBService
import com.tiagohs.domain.services.TMDBService
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun providerHomePresenter(localService: LocalService): HomePresenter = HomePresenterImpl(localService)

    @Provides
    fun providerGlossaryPresenter(localService: LocalService): GlossaryPresenter = GlossaryPresenterImpl(localService)

    @Provides
    fun providerMainTopicsPresenter(localService: LocalService): MainTopicsPresenter = MainTopicsPresenterImpl(localService)

    @Provides
    fun providerMilMoviesPresentationPresenter(tmdbService: TMDBService): MilMoviesPresentationPresenter = MilMoviesPresentationPresenterImpl(tmdbService)

    @Provides
    fun providerPresentationPresenter(localService: LocalService): PresentationPresenter = PresentationPresenterImpl(localService)

    @Provides
    fun providerHistoryPagePresenter(localService: LocalService): HistoryPagePresenter = HistoryPagePresenterImpl(localService)

    @Provides
    fun providerMovieDetailsPresenter(tmdbService: TMDBService, localService: LocalService, omdbService: OMDBService): MovieDetailsPresenter = MovieDetailsPresenterImpl(tmdbService, omdbService, localService)

    @Provides
    fun providerPersonDetailsPresenter(tmdbService: TMDBService, localService: LocalService): PersonDetailsPresenter = PersonDetailsPresenterImpl(tmdbService, localService)

    @Provides
    fun providerTimelinePresenter(localService: LocalService): TimelinePresenter = TimelinePresenterImpl(localService)

    @Provides
    fun providerTimelinePagePresenter(localService: LocalService): TimelinePagePresenter = TimelinePagePresenterImpl(localService)

    @Provides
    fun providerReferencePresenter(localService: LocalService): ReferencePresenter = ReferencePresenterImpl(localService)

    @Provides
    fun providerUniversalLinkPresenter(localService: LocalService): UniversalLinkPresenter = UniversalLinkPresenterImpl(localService)
}