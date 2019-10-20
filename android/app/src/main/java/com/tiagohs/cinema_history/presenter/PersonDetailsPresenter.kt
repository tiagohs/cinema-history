package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.PersonDetailsView

interface PersonDetailsPresenter: IPresenter<PersonDetailsView> {

    fun fetchPersonDetails(personId: Int)
}