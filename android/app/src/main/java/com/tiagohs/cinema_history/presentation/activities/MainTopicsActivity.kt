package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.entities.main_topics.DirectorsMainTopic
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.domain.presenter.MainTopicsPresenter
import com.tiagohs.cinema_history.presentation.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.domain.views.MainTopicsView
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.activity_main_topics.*
import javax.inject.Inject


class MainTopicsActivity: BaseActivity(), MainTopicsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_main_topics
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MainTopicsPresenter

    private var mainTopicsType: MainTopicsType? = null
    private var isDarkMode = false
    private var adapter: MainTopicsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchMainTopics(mainTopicsType)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun setupArguments() {
        val type = intent.getStringExtra(MAIN_TOPIC_TYPE)

        if (type != null) {
            mainTopicsType = MainTopicsType.getContentType(type)
        }

        isDarkMode = intent.getBooleanExtra(DARK_MODE, false)
    }

    override fun setupScreenTitle() {
        val titleRes = when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> R.string.history_cinema_title
            MainTopicsType.MIL_MOVIES -> R.string.mil_movies_title
            MainTopicsType.TIMELINE -> R.string.timeline_title
            MainTopicsType.DIRECTORS -> R.string.directors_title
            else -> R.string.history_cinema_title
        }

        toolbarTitle.text = getResourceString(titleRes)
    }

    override fun setupScreenLayout() {
        if (isDarkMode) {
            setupDarkScreen()
            return
        }

        setupLightScreen()
    }

    private fun setupDarkScreen() {
        setScreenBackgroundColor(R.color.md_black_1000)

        loadViewContainer.addView(LayoutInflater.from(this).inflate(R.layout.load_view_main_topics_card_dark, null, false))
        loadViewContainer.addView(LayoutInflater.from(this).inflate(R.layout.load_view_main_topics_card_dark, null, false))
    }

    private fun setupLightScreen() {
        val whiteColor = getResourceColor(R.color.md_white_1000)
        val blackColor = getResourceColor(R.color.md_black_1000)

        toolbar.setBackgroundColor(whiteColor)
        toolbarTitle.setTextColor(blackColor)
        toolbar.navigationIcon?.setColorFilter(blackColor, PorterDuff.Mode.SRC_ATOP)

        mainTopicsList.setBackgroundColor(whiteColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        setStatusBarColor(R.color.md_white_1000)

        loadViewContainer.addView(LayoutInflater.from(this).inflate(R.layout.load_view_main_topics_card_light, null, false))
        loadViewContainer.addView(LayoutInflater.from(this).inflate(R.layout.load_view_main_topics_card_light, null, false))
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
    }

    override fun bindMainTopics(mainTopics: List<MainTopic>) {
        val mainTopicsType = mainTopicsType?: return
        adapter = MainTopicsAdapter(mainTopicsType, mainTopics, isDarkMode)
        adapter?.onMainTopicSelected = { mainTopic, _ -> onMainTopicSelected(mainTopic) }

        mainTopicsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainTopicsList.adapter = adapter

        mainTopicsList.startAnimation(AnimationUtils.createFadeInAnimation(300, 200))
    }

    private fun onMainTopicSelected(mainTopic: MainTopic) {
        val intent = when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> PresentationActivity.newInstance(this, mainTopic as MainTopicItem)
            MainTopicsType.MIL_MOVIES -> MilMoviesPresentationActivity.newIntent(mainTopic as MilMoviesMainTopic, this)
            MainTopicsType.DIRECTORS -> PersonDetailsActivity.newIntent(this, (mainTopic as DirectorsMainTopic).personId)
            else -> return
        }

        startActivityWithSlideAnimation(intent)
    }

    override fun startLoading() {
        mainTopicsList.hide()

        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        mainTopicsList.show()

        loadView.hideShimmer()
        loadView.hide()
    }

    companion object {

        const val MAIN_TOPIC_TYPE = "MAIN_TOPIC_TYPE"
        const val DARK_MODE = "DARK_MODE"

        fun newIntent(mainTopicType: MainTopicsType, context: Context, darkMode: Boolean = false) : Intent {
            val intent = Intent(context, MainTopicsActivity::class.java)

            intent.putExtra(MAIN_TOPIC_TYPE, mainTopicType.type)
            intent.putExtra(DARK_MODE, darkMode)

            return intent
        }
    }
}