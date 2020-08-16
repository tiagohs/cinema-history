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
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
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
        contentList.apply {
            layoutManager = LinearLayoutManager(this@ReferenceActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(this@ReferenceActivity), SpaceOffsetDecoration.LEFT))
            adapter = ReferencesAdapter(references).apply {
                onLinkClick = { openLink(it) }
            }
        }
    }

    override fun startLoading() {
        contentList.hide()

        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        contentList.show()

        loadView.hideShimmer()
        loadView.hide()
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, ReferenceActivity::class.java)
    }
}