package com.tiagohs.cinema_history.presentation.activities

import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.content.Context
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityGlossaryBinding
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
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
import javax.inject.Inject

class GlossaryActivity : BaseActivity<ActivityGlossaryBinding>(), GlossaryView {

    @Inject
    lateinit var presenter: GlossaryPresenter

    @Inject
    lateinit var settingManager: SettingsManager

    override fun inflateBinding(inflater: LayoutInflater) = ActivityGlossaryBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tablets: lista numa coluna centralizada (o índice A–Z continua na borda).
        binding.contentList.limitContentWidth(R.dimen.ls_list_max_width)
        binding.loadView.limitContentWidth(R.dimen.ls_list_max_width)

        setupToolbar(binding.toolbar, displayShowTitleEnabled = true)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchPageContent()
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
    }

    override fun bindGlossaryContent(glossaryList: List<Glossary>) {
        binding.contentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = GlossaryAdapter(glossaryList, null, settingManager.getMovieLanguage()).apply {
                presentScreen = { presentScreen(it) }
                onMovieClicked = { onMovieSelected(it) }
                onPersonClicked = { onPersonClicked(it) }
                onLinkClicked = { onLinkClicked(it) }
            }
        }

        binding.fastScrollerView.apply {
            setupWithRecyclerView(
                binding.contentList,
                { position ->
                    glossaryList[position]
                        ?.let { item ->
                            FastScrollItemIndicator.Text(
                                item
                                    .name
                                    .substring(0, 1)
                                    .uppercase()
                            )
                        }
                }
            )
        }

        binding.fastScrollerThumbView.apply {
            setupWithFastScroller(binding.fastScrollerView)
        }
    }

    override fun startLoading() {
        binding.contentList.hide()
        //fastScrollerView.hide()
        //fastScrollerThumbView.hide()

        binding.loadView.showShimmer(true)
        binding.loadView.show()
    }

    override fun hideLoading() {
        binding.contentList.show()
        //fastScrollerView.show()
        //fastScrollerThumbView.show()

        binding.loadView.hideShimmer()
        binding.loadView.hide()
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