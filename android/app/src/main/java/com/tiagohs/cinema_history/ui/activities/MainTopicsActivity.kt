package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.MainTopicItem
import com.tiagohs.cinema_history.presenter.MainTopicsPresenter
import com.tiagohs.cinema_history.ui.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.MainTopicsView
import kotlinx.android.synthetic.main.activity_main_topics.*
import javax.inject.Inject


class MainTopicsActivity: BaseActivity(), MainTopicsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_main_topics
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MainTopicsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        getApplicationComponent()?.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mainTopicsType = intent.getSerializableExtra(MAIN_TOPIC_TYPE) as? MainTopicsType
        val darkMode = intent.getBooleanExtra(DARK_MODE, false)

        when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> {
                toolbarTitle.text = "Historia do Cinema"
            }
            MainTopicsType.MILL_MOVIES -> {
                toolbarTitle.text = "1001 Filmes"
            }
            MainTopicsType.TIMELINE -> {
                toolbarTitle.text = "Timeline do Cinema"
            }
        }

        if (darkMode) {
            val view = this.window.decorView
            view.setBackgroundColor(resources.getColor(R.color.md_black_1000))
        }

        presenter.onBindView(this)
        presenter.fetchMainTopics()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun bindMainTopics(mainTopics: List<MainTopic>) {
        val adapter = MainTopicsAdapter(this, mainTopics)
        adapter.onMainTopicSelected = {
            startActivity(PresentationActivity.newInstance(this, it))
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