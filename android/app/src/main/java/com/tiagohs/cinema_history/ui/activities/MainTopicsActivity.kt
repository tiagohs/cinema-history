package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.setScreenBackgroundColor
import com.tiagohs.cinema_history.helpers.extensions.setStatusBarColor
import com.tiagohs.cinema_history.helpers.extensions.startActivityWithSlideAnimation
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
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
        mainTopicsType = intent.getSerializableExtra(MAIN_TOPIC_TYPE) as? MainTopicsType
        isDarkMode = intent.getBooleanExtra(DARK_MODE, false)
    }

    override fun setupScreenTitle() {
        val titleRes = when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> R.string.history_cinema_title
            MainTopicsType.MIL_MOVIES -> R.string.mil_movies_title
            MainTopicsType.TIMELINE -> R.string.timeline_title
            else -> R.string.history_cinema_title
        }

        toolbarTitle.text = getString(titleRes)
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
        adapter = MainTopicsAdapter(this, mainTopicsType, mainTopics, isDarkMode)
        adapter?.onMainTopicSelected = { mainTopic, view -> onMainTopicSelected(mainTopic, view) }

        mainTopicsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainTopicsList.adapter = adapter

        mainTopicsList.startAnimation(AnimationUtils.createFadeInAnimation(300, 200))
    }

    private fun onMainTopicSelected(mainTopic: MainTopic, view: View?) {
        val intent = when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> PresentationActivity.newInstance(this, mainTopic as MainTopicItem)
            MainTopicsType.MIL_MOVIES -> MilMoviesPresentationActivity.newIntent(mainTopic as MilMoviesMainTopic, this)
            else -> return
        }

        startActivityWithSlideAnimation(intent)
    }

    override fun startLoading() {
        mainTopicsList.visibility = GONE

        loadView.startShimmer()
        loadView.visibility = VISIBLE
    }

    override fun hideLoading() {
        mainTopicsList.visibility = VISIBLE

        loadView.stopShimmer()
        loadView.visibility = GONE
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