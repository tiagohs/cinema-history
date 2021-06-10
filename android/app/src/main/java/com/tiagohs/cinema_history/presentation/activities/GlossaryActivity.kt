package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.GlossaryAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.domain.presenter.GlossaryPresenter
import com.tiagohs.domain.views.GlossaryView
import com.tiagohs.entities.Glossary
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import kotlinx.android.synthetic.main.activity_glossary.*
import kotlinx.android.synthetic.main.activity_glossary.toolbar
import javax.inject.Inject

class GlossaryActivity : BaseActivity(), GlossaryView {

    @Inject
    lateinit var presenter: GlossaryPresenter

    @Inject
    lateinit var settingManager: SettingsManager

    override fun onGetLayoutViewId(): Int = R.layout.activity_glossary
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar(toolbar, displayShowTitleEnabled = true)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchPageContent()
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
    }

    override fun bindGlossaryContent(glossaryList: List<Glossary>) {
        contentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = GlossaryAdapter(glossaryList, null, settingManager.getMovieLanguage()).apply {
                presentScreen = { presentScreen(it) }
                onMovieClicked = { onMovieSelected(it) }
                onPersonClicked = { onPersonClicked(it) }
                onLinkClicked = { onLinkClicked(it) }
            }
        }

        fastScrollerView.apply {
            setupWithRecyclerView(
                contentList,
                { position ->
                    glossaryList[position]
                        ?.let { item ->
                            FastScrollItemIndicator.Text(
                                item
                                    .name
                                    .substring(0, 1)
                                    .toUpperCase()
                            )
                        }
                }
            )
        }

        fastScrollerThumbView.apply {
            setupWithFastScroller(fastScrollerView)
        }
    }

    override fun startLoading() {
        contentList.hide()
        //fastScrollerView.hide()
        //fastScrollerThumbView.hide()

        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        contentList.show()
        //fastScrollerView.show()
        //fastScrollerThumbView.show()

        loadView.hideShimmer()
        loadView.hide()
    }


    private fun presentScreen(intent: Intent) {
        startActivity(intent)
    }

    private fun onMovieSelected(movieId: Int) {
        startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(this, movieId))
    }

    private fun onPersonClicked(personId: Int) {
        startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(this, personId))
    }

    private fun onLinkClicked(url: String) {
        openLink(url)
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, GlossaryActivity::class.java)
    }
}