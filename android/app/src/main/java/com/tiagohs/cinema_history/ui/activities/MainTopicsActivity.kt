package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.presenter.MainTopicsPresenter
import com.tiagohs.cinema_history.ui.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.MainTopicsView
import kotlinx.android.synthetic.main.activity_main_topics.*
import kotlinx.android.synthetic.main.activity_main_topics.toolbar
import kotlinx.android.synthetic.main.activity_mil_movies_presentation.*
import javax.inject.Inject


class MainTopicsActivity: BaseActivity(), MainTopicsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_main_topics
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MainTopicsPresenter

    private var mainTopicsType: MainTopicsType? = null
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        getApplicationComponent()?.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainTopicsType = intent.getSerializableExtra(MAIN_TOPIC_TYPE) as? MainTopicsType
        isDarkMode = intent.getBooleanExtra(DARK_MODE, false)

        when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> {
                toolbarTitle.text = "Historia do Cinema"
            }
            MainTopicsType.MIL_MOVIES -> {
                toolbarTitle.text = "1001 Filmes"
            }
            MainTopicsType.TIMELINE -> {
                toolbarTitle.text = "Timeline do Cinema"
            }
        }

        if (isDarkMode) {
            val view = this.window.decorView
            view.setBackgroundColor(resources.getColor(R.color.md_black_1000))
        } else {
            toolbar.setBackgroundColor(resources.getColor(R.color.md_white_1000))
            toolbarTitle.setTextColor(resources.getColor(R.color.md_black_1000))
            toolbar.navigationIcon?.setColorFilter(resources.getColor(R.color.md_black_1000), PorterDuff.Mode.SRC_ATOP)
        }

        presenter.onBindView(this)
        presenter.fetchMainTopics(mainTopicsType)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun bindMainTopics(mainTopics: List<MainTopic>) {
        val mainTopicsType = mainTopicsType?: return
        val adapter = MainTopicsAdapter(this, mainTopicsType, mainTopics, isDarkMode)
        adapter.onMainTopicSelected = {

            when (mainTopicsType) {
                MainTopicsType.HISTORY_CINEMA -> {
                    startActivity(PresentationActivity.newInstance(this, it as MainTopicItem))
                }
                MainTopicsType.MIL_MOVIES -> {
                    startActivity(MilMoviesPresentationActivity.newIntent(it as MilMoviesMainTopic, this))
                }
                MainTopicsType.TIMELINE -> {

                }
            }

        }

        mainTopicsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainTopicsList.adapter = adapter
    }

    companion object {

        const val MAIN_TOPIC_TYPE = "MAIN_TOPIC_TYPE"
        const val DARK_MODE = "DARK_MODE"

        fun newIntent(mainTopicType: MainTopicsType, context: Context, darkMode: Boolean = false) : Intent {
            val intent = Intent(context, MainTopicsActivity::class.java)

            intent.putExtra(MAIN_TOPIC_TYPE, mainTopicType)
            intent.putExtra(DARK_MODE, darkMode)

            return intent
        }
    }
}