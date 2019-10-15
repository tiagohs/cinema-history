package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.ui.configs.IView

interface HistoryPageView: IView {

    fun setupHeader()
    fun setupArguments()

    fun bindPageContent(pageContent: Page)
}