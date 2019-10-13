package com.tiagohs.cinema_history.dagger

import com.tiagohs.cinema_history.dagger.modules.AppModule
import com.tiagohs.cinema_history.dagger.modules.PresenterModule
import com.tiagohs.cinema_history.dagger.modules.ServiceModule
import com.tiagohs.cinema_history.ui.activities.HomeActivity
import com.tiagohs.cinema_history.ui.activities.MainTopicsActivity
import com.tiagohs.cinema_history.ui.activities.MilMoviesPresentationActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    PresenterModule::class,
    ServiceModule::class])
@Singleton
interface AppComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(mainTopicsActivity: MainTopicsActivity)
    fun inject(milMoviesPresentation: MilMoviesPresentationActivity)
}