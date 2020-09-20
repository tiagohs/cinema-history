package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.GlossaryView

interface GlossaryPresenter : IPresenter<GlossaryView> {
    fun fetchPageContent()
}