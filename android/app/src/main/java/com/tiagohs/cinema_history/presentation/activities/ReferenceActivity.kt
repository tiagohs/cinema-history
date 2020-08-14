package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.ReferencesAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.presenter.ReferencePresenter
import com.tiagohs.domain.views.ReferenceView
import com.tiagohs.entities.references.Reference
import kotlinx.android.synthetic.main.activity_references.*
import kotlinx.android.synthetic.main.activity_setting.toolbar
import javax.inject.Inject

class ReferenceActivity : BaseActivity(), ReferenceView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_references
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: ReferencePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar(toolbar, displayShowTitleEnabled = true)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchReferences()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun bindReference(references: List<Reference>) {
        contentList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        contentList.adapter = ReferencesAdapter(this, references)
    }

    override fun startLoading() {
        contentList.visibility = View.GONE

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        contentList.visibility = View.VISIBLE

        loadView.startShimmer()
        loadView.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, ReferenceActivity::class.java)
    }
}