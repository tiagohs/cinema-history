package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.Glossary

interface GlossaryView : IView {
    fun bindGlossaryContent(glossaryList: List<Glossary>)

    fun startLoading()
    fun hideLoading()
}