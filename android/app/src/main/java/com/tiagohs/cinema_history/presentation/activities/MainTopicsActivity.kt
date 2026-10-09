package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import com.tiagohs.cinema_history.databinding.ActivityMainTopicsBinding
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdPlacement
import com.tiagohs.cinema_history.ads.adapterWithNativeAd
import com.tiagohs.cinema_history.presentation.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.presenter.MainTopicsPresenter
import com.tiagohs.domain.views.MainTopicsView
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.main_topics.*
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import javax.inject.Inject


class MainTopicsActivity: BaseActivity<ActivityMainTopicsBinding>(), MainTopicsView {

    override fun inflateBinding(inflater: LayoutInflater) = ActivityMainTopicsBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MainTopicsPresenter

    private var mainTopicsType: MainTopicsType? = null
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(binding.toolbar)

        presenter.onBindView(this)
        presenter.fetchMainTopics(mainTopicsType)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
            MainTopicsType.AWARDS -> R.string.awards_title
            MainTopicsType.DIRECTORS -> R.string.directors_title
            else -> R.string.history_cinema_title
        }

        binding.toolbarTitle.text = getResourceString(titleRes)
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

        binding.loadViewContainer.addView(
            LayoutInflater.from(this).inflate(
                R.layout.load_view_main_topics_card_dark,
                null,
                false
            )
        )
        binding.loadViewContainer.addView(
            LayoutInflater.from(this).inflate(
                R.layout.load_view_main_topics_card_dark,
                null,
                false
            )
        )
    }

    private fun setupLightScreen() {
        // "Modo claro" da lista: barra e fundo acompanham o tema do app (branco no claro, escuro no escuro).
        val whiteColor = getResourceColor(R.color.daynight_background)
        val blackColor = getResourceColor(R.color.daynight_text_primary)

        binding.toolbar.setBackgroundColor(whiteColor)
        binding.toolbarTitle.setTextColor(blackColor)
        binding.toolbar.navigationIcon?.setColorFilter(blackColor, PorterDuff.Mode.SRC_ATOP)

        binding.mainTopicsList.setBackgroundColor(whiteColor)

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkThemeActive()

        setStatusBarColor(R.color.daynight_background)

        binding.loadViewContainer.addView(
            LayoutInflater.from(this).inflate(
                R.layout.load_view_main_topics_card_light,
                null,
                false
            )
        )
        binding.loadViewContainer.addView(
            LayoutInflater.from(this).inflate(
                R.layout.load_view_main_topics_card_light,
                null,
                false
            )
        )
    }

    override fun onDestroy() {
        presenter.onUnbindView()

        super.onDestroy()
    }

    override fun bindMainTopics(mainTopics: List<MainTopic>) {
        val mainTopicsType = mainTopicsType?: return
        // Anúncio nativo depois do 3º item da lista (eras, prêmios, 1001 filmes, diretores).
        val listAdapter = adapterWithNativeAd(mainTopics, after = 3, AdPlacement.LISTS, this) { items ->
            MainTopicsAdapter(mainTopicsType, items, isDarkMode).apply {
                onMainTopicSelected = { mainTopic, _ -> onMainTopicSelected(mainTopic) }
            }
        }

        binding.mainTopicsList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.mainTopicsList.adapter = listAdapter

        binding.mainTopicsList.startAnimation(AnimationUtils.createFadeInAnimation(300, 200))
    }

    private fun onMainTopicSelected(mainTopic: MainTopic) {
        val intent = when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> PresentationActivity.newInstance(
                this,
                mainTopic as MainTopicItem
            )
            MainTopicsType.MIL_MOVIES -> MilMoviesPresentationActivity.newIntent(
                mainTopic as MilMoviesMainTopic,
                this
            )
            MainTopicsType.AWARDS -> AwardActivity.newIntent(
                mainTopic as AwardMainTopic,
                this
            )
            MainTopicsType.DIRECTORS -> PersonDetailsActivity.newIntent(
                this,
                (mainTopic as DirectorsMainTopic).personId
            )
            else -> return
        }

        startActivityWithSlideRightToLeftAnimation(intent)
    }

    override fun startLoading() {
        binding.mainTopicsList.hide()

        binding.loadView.showShimmer(true)
        binding.loadView.show()
    }

    override fun hideLoading() {
        binding.mainTopicsList.show()

        binding.loadView.hideShimmer()
        binding.loadView.hide()
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