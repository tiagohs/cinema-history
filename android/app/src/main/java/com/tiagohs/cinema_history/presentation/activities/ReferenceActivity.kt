package com.tiagohs.cinema_history.presentation.activities

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.content.Context
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityReferencesBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdPlacement
import com.tiagohs.cinema_history.ads.adapterWithNativeAd
import com.tiagohs.cinema_history.presentation.adapters.ReferencesAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.presenter.ReferencePresenter
import com.tiagohs.domain.views.ReferenceView
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceResult
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import javax.inject.Inject

class ReferenceActivity : BaseActivity<ActivityReferencesBinding>(), ReferenceView {

    override fun inflateBinding(inflater: LayoutInflater) = ActivityReferencesBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: ReferencePresenter

    private var references: List<ReferenceResult> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tablets: conteúdo numa coluna centralizada.
        binding.pageContentListContainer.limitContentWidth(R.dimen.ls_list_max_width)
        binding.loadView.limitContentWidth(R.dimen.ls_list_max_width)

        setupToolbar(binding.toolbar, displayShowTitleEnabled = true)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchReferences()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun bindReference(references: List<ReferenceResult>) {
        this.references = references

        val typesSpinnerList = references.map { it.name }

        binding.spinner.adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, typesSpinnerList)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val referencesResult = references.getOrNull(position) ?: return

                // A aba "Tudo" não tem referências próprias: agrega as das outras abas (independe do idioma).
                if (referencesResult.references.isNullOrEmpty()) {
                    val all = ArrayList<Reference>()

                    references.forEach {
                        if (!it.references.isNullOrEmpty()) {
                            all.addAll(it.references!!)
                        }
                    }

                    setupReviewList(all)
                    return
                }

                setupReviewList(referencesResult.references ?: emptyList())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupReviewList(references: List<Reference>) {
        binding.contentList.apply {
            layoutManager = LinearLayoutManager(this@ReferenceActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterWithNativeAd(references, after = 4, AdPlacement.LISTS, this@ReferenceActivity) { items ->
                ReferencesAdapter(items).apply {
                    onLinkClick = { openLink(it) }
                }
            }
        }
    }

    override fun startLoading() {
        binding.contentList.hide()

        binding.loadView.showShimmer(true)
        binding.loadView.show()
    }

    override fun hideLoading() {
        binding.contentList.show()

        binding.loadView.hideShimmer()
        binding.loadView.hide()
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, ReferenceActivity::class.java)
    }
}