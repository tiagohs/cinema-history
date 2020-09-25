package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView

interface AwardView : IView {
    fun startLoading()
    fun hideLoading()
}