package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.references.Reference

interface ReferenceView : IView {

    fun bindReference(references: List<Reference>)

    fun startLoading()
    fun hideLoading()
}