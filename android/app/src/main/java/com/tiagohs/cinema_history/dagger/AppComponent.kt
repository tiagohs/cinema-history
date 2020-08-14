package com.tiagohs.cinema_history.dagger

import com.tiagohs.cinema_history.dagger.modules.AppModule
import com.tiagohs.cinema_history.dagger.modules.PresenterModule
import com.tiagohs.cinema_history.dagger.modules.RetrofitModule
import com.tiagohs.cinema_history.dagger.modules.ServiceModule
import com.tiagohs.cinema_history.presentation.activities.*
import com.tiagohs.cinema_history.presentation.fragments.HistoryPageFragment
import com.tiagohs.cinema_history.presentation.fragments.TimelineFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    PresenterModule::class,
    ServiceModule::class,
    RetrofitModule::class])
@Singleton
interface AppComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(mainTopicsActivity: MainTopicsActivity)
    fun inject(milMoviesPresentation: MilMoviesPresentationActivity)
    fun inject(presentationActivity: PresentationActivity)
    fun inject(historyPagesActivity: HistoryPagesActivity)
    fun inject(historyPageFragment: HistoryPageFragment)
    fun inject(movieDetailsActivity: MovieDetailsActivity)
    fun inject(personDetailsActivity: PersonDetailsActivity)
    fun inject(timelineActivity: TimelineActivity)
    fun inject(timelineFragment: TimelineFragment)
    fun inject(referenceActivity: ReferenceActivity)
}