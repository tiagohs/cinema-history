package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.PersonDetailsView

interface PersonDetailsPresenter: IPresenter<PersonDetailsView> {

    fun fetchPersonDetails(personId: Int, languageToUse: String)
}