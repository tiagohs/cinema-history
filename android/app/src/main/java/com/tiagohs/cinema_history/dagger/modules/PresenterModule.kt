package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.cinema_history.presenter.MainTopicsPresenter
import com.tiagohs.cinema_history.presenter.MainTopicsPresenterImpl
import com.tiagohs.cinema_history.services.LocalService
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun providerHomePresenter(localService: LocalService): MainTopicsPresenter = MainTopicsPresenterImpl(localService)
}