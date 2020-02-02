package com.tiagohs.domain.views

import com.tiagohs.entities.Page
import com.tiagohs.domain.views.configs.IView

interface HistoryPageView: IView {

    fun setupHeader()
    fun setupArguments()

    fun bindPageContent(pageContent: com.tiagohs.entities.Page)
}