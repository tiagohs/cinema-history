package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.AwardView
import javax.inject.Inject

class AwardPresenterImpl @Inject constructor(
    val localService: LocalService
): AwardPresenter, BasePresenter<AwardView>() {

}