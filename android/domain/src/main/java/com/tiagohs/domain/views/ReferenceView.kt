package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceResult

interface ReferenceView : IView {

    fun bindReference(references: List<ReferenceResult>)

    fun startLoading()
    fun hideLoading()
}