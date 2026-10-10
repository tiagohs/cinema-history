package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import com.tiagohs.cinema_history.databinding.ActivityMainTopicsBinding
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdPlacement
import com.tiagohs.cinema_history.ads.NativeAdAdapter
import com.tiagohs.cinema_history.ads.adapterWithNativeAd
import com.tiagohs.cinema_history.presentation.adapters.directors.DirectorEra
import com.tiagohs.cinema_history.presentation.adapters.directors.DirectorRegion
import com.tiagohs.cinema_history.presentation.adapters.directors.DirectorsAdapter
import com.tiagohs.cinema_history.presentation.adapters.directors.DirectorsCatalog
import com.tiagohs.cinema_history.presentation.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.BalancedGridSpanLookup
import com.tiagohs.cinema_history.presentation.configs.LargeScreen
import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import com.tiagohs.entities.enums.MainTopicItemLayoutType
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

    // Tela de diretores: filtros escolhidos (época/"todos"/"em alta" e região) e adapters da grade.
    private var directors: List<DirectorsMainTopic> = emptyList()
    private var directorsFilter = DirectorsCatalog.FILTER_ALL
    private var directorsRegion: String? = null
    private var directorsHead: DirectorsAdapter? = null
    private var directorsTail: DirectorsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            directorsFilter = it.getString(STATE_DIRECTORS_FILTER) ?: DirectorsCatalog.FILTER_ALL
            directorsRegion = it.getString(STATE_DIRECTORS_REGION)
        }

        getApplicationComponent()?.inject(this)
        setupToolbar(binding.toolbar)

        presenter.onBindView(this)
        presenter.fetchMainTopics(mainTopicsType)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_DIRECTORS_FILTER, directorsFilter)
        outState.putString(STATE_DIRECTORS_REGION, directorsRegion)
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

        if (mainTopicsType == MainTopicsType.DIRECTORS) {
            bindDirectors(mainTopics.filterIsInstance<DirectorsMainTopic>())
            return
        }

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
        setupLargeScreenList(mainTopicsType)

        binding.mainTopicsList.startAnimation(AnimationUtils.createFadeInAnimation(300, 200))
    }

    /**
     * Tablets (res/values-sw600dp*): prêmios, diretores e 1001 filmes viram uma grade de cartões
     * (2 colunas em medium, 3 em expanded; citações e anúncio ocupam a linha toda). As eras da
     * História do Cinema são uma sequência editorial (cartões de formatos diferentes intercalados
     * com citações), então continuam numa coluna, só que centralizada e com largura máxima.
     * No celular nada muda.
     */
    private fun setupLargeScreenList(type: MainTopicsType) {
        val list = binding.mainTopicsList
        if (!LargeScreen.isLarge(this)) return

        val sidePadding = resources.getDimensionPixelSize(R.dimen.ls_list_side_padding)
        list.setPaddingRelative(sidePadding, list.paddingTop, sidePadding, list.paddingBottom)
        list.clipToPadding = false

        if (type == MainTopicsType.HISTORY_CINEMA) {
            list.limitContentWidth(R.dimen.ls_list_max_width)
            binding.loadView.limitContentWidth(R.dimen.ls_list_max_width)
            return
        }

        BalancedGridSpanLookup.applyIfMultiColumn(list, R.integer.ls_card_columns) { position ->
            val (adapter, local) = BalancedGridSpanLookup.resolve(list.adapter, position) ?: return@applyIfMultiColumn true
            val topicsAdapter = adapter as? MainTopicsAdapter ?: return@applyIfMultiColumn true // anúncio nativo
            topicsAdapter.list.getOrNull(local)?.layoutType == MainTopicItemLayoutType.QUOTE
        }
    }

    // ---------------------------------------------------------------- Diretores

    /**
     * Lista de diretores em grade (2 colunas no celular, 3/4 em tablets), agrupada por época, com o
     * carrossel "Em alta" no topo e filtros fixos de época e região logo abaixo da barra.
     */
    private fun bindDirectors(items: List<DirectorsMainTopic>) {
        directors = items

        binding.appBar.layoutParams = binding.appBar.layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
        binding.directorsFilters.visibility = View.VISIBLE
        setupDirectorsChips()

        val columns = resources.getInteger(R.integer.directors_grid_columns)
        val padding = resources.getDimensionPixelSize(R.dimen.directors_screen_padding)
        val head = DirectorsAdapter { openDirector(it) }
        val tail = DirectorsAdapter { openDirector(it) }
        directorsHead = head
        directorsTail = tail

        val list = binding.mainTopicsList
        list.setPaddingRelative(padding, padding / 2, padding, padding)
        list.clipToPadding = false
        list.layoutManager = GridLayoutManager(this, columns).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val (adapter, local) = BalancedGridSpanLookup.resolve(list.adapter, position) ?: return columns
                    val directorsAdapter = adapter as? DirectorsAdapter ?: return columns // anúncio nativo
                    return if (directorsAdapter.isFullSpan(local)) columns else 1
                }
            }
        }
        // Anúncio nativo logo depois do carrossel "Em alta" (com filtros, vai para o fim da lista).
        list.adapter = ConcatAdapter(head, NativeAdAdapter(AdPlacement.LISTS, this), tail)

        renderDirectors()
        list.startAnimation(AnimationUtils.createFadeInAnimation(300, 200))
    }

    private fun renderDirectors() {
        val rows = DirectorsCatalog.rows(directors, directorsFilter, directorsRegion)
        val adAt = DirectorsCatalog.adPosition(rows)

        if (adAt >= 0) {
            directorsHead?.submitList(rows.take(adAt))
            directorsTail?.submitList(rows.drop(adAt))
        } else {
            directorsHead?.submitList(rows)
            directorsTail?.submitList(emptyList())
        }
    }

    private fun setupDirectorsChips() {
        val eras = listOf(
            DirectorsCatalog.FILTER_ALL to getString(R.string.directors_filter_all),
            DirectorsCatalog.FILTER_TRENDING to getString(R.string.directors_filter_trending)
        ) + DirectorEra.values().map { it.key to getString(it.title) }
        val regions = listOf<Pair<String?, String>>(null to getString(R.string.directors_region_all)) +
            DirectorRegion.values().map { it.key to getString(it.label) }

        fillChips(binding.directorsEraChips, eras, directorsFilter) {
            directorsFilter = it ?: DirectorsCatalog.FILTER_ALL
            onDirectorsFilterChanged()
        }
        fillChips(binding.directorsRegionChips, regions, directorsRegion) {
            directorsRegion = it
            onDirectorsFilterChanged()
        }
    }

    private fun fillChips(group: ChipGroup, options: List<Pair<String?, String>>, selected: String?, onSelected: (String?) -> Unit) {
        group.setOnCheckedStateChangeListener(null)
        group.removeAllViews()

        options.forEach { (key, label) ->
            val chip = layoutInflater.inflate(R.layout.view_directors_filter_chip, group, false) as Chip
            chip.id = View.generateViewId()
            chip.tag = key
            chip.text = label
            group.addView(chip)
            if (key == selected) chip.isChecked = true
        }

        group.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val chip = checkedIds.firstOrNull()?.let { chipGroup.findViewById<Chip>(it) } ?: return@setOnCheckedStateChangeListener
            onSelected(chip.tag as? String)
        }
    }

    private fun onDirectorsFilterChanged() {
        renderDirectors()
        binding.mainTopicsList.scrollToPosition(0)
        binding.appBar.setExpanded(true, true)
    }

    private fun openDirector(director: DirectorsMainTopic) {
        startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(this, director.personId))
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
        private const val STATE_DIRECTORS_FILTER = "STATE_DIRECTORS_FILTER"
        private const val STATE_DIRECTORS_REGION = "STATE_DIRECTORS_REGION"

        fun newIntent(mainTopicType: MainTopicsType, context: Context, darkMode: Boolean = false) : Intent {
            val intent = Intent(context, MainTopicsActivity::class.java)

            intent.putExtra(MAIN_TOPIC_TYPE, mainTopicType.type)
            intent.putExtra(DARK_MODE, darkMode)

            return intent
        }
    }
}