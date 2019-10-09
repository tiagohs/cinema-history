package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.cinema_history.presenter.HomePresenter
import com.tiagohs.cinema_history.presenter.HomePresenterImpl
import com.tiagohs.cinema_history.services.LocalService
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun providerHomePresenter(localService: LocalService): HomePresenter = HomePresenterImpl(localService)
}