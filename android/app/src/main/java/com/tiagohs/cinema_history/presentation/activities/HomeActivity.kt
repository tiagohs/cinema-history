package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdsManager
import com.tiagohs.cinema_history.support.Supporter
import com.tiagohs.cinema_history.databinding.ActivityHomeBinding
import com.tiagohs.cinema_history.databinding.ViewHomeLinkRowBinding
import com.tiagohs.cinema_history.presentation.adapters.home.HomeDestinationsAdapter
import com.tiagohs.cinema_history.presentation.adapters.home.HomeErasAdapter
import com.tiagohs.cinema_history.presentation.adapters.home.HomeImages
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.domain.presenter.HomePresenter
import com.tiagohs.domain.views.HomeView
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import javax.inject.Inject

/**
 * Home em Material Design 3.
 *
 * - Destaque no topo para a História do Cinema: a última era aberta ("Continue lendo") ou,
 *   na primeira visita, o capítulo mais recente.
 * - Carrossel M3 com todas as eras (maintopics.json).
 * - Grade com as outras áreas (homecontent.json) e atalhos para Glossário, Referências,
 *   Configurações e Sobre.
 *
 * Todas as navegações são as mesmas que já existiam (MainTopicsActivity, TimelineActivity,
 * PresentationActivity etc.). A Home não tem anúncios.
 */
class HomeActivity : BaseActivity<ActivityHomeBinding>(), HomeView {

    @Inject
    lateinit var presenter: HomePresenter

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun inflateBinding(inflater: LayoutInflater) = ActivityHomeBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = R.menu.menu_main

    private var historyEras: List<MainTopicItem> = emptyList()
    private var historyDarkMode = true
    private var heroEra: MainTopicItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchHomeContent()

        AdsManager.gatherConsent(this)

        Supporter.addListener(supporterListener)
    }

    /** Compra/restauração do apoio ou país da conta mudou: atualiza o item "Apoie o app". */
    private val supporterListener: () -> Unit = { invalidateOptionsMenu() }

    override fun onResume() {
        super.onResume()

        // Ao voltar de uma era, o destaque passa a ser "Continue lendo" daquela era.
        if (historyEras.isNotEmpty()) bindHero()
    }

    override fun onDestroy() {
        Supporter.removeListener(supporterListener)
        presenter.onUnbindView()

        super.onDestroy()
    }

    /** Idioma e tema ficam visíveis na toolbar; o resto vai para o menu de três pontos. */
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menu.findItem(R.id.action_language)?.let { item ->
            val languages = com.tiagohs.helpers.utils.ContentLanguage.ENABLED
            if (languages.size < 2) {
                item.isVisible = false
            } else {
                val current = com.tiagohs.helpers.utils.ContentLanguage.current()
                val chip = item.actionView?.findViewById<android.widget.TextView>(R.id.languageChip)
                chip?.text = current.uppercase()
                val label = getString(R.string.action_language, java.util.Locale(current).let { it.getDisplayLanguage(it) })
                item.title = label
                chip?.contentDescription = label
                chip?.setOnClickListener { switchToNextLanguage(languages, current) }
            }
        }

        menu.findItem(R.id.action_support)?.let { item ->
            val supporter = Supporter.isSupporter(this)
            item.isVisible = supporter || Supporter.isOfferAvailable(this)
            item.setTitle(if (supporter) R.string.support_menu_supporter else R.string.support_menu)
        }

        menu.findItem(R.id.action_theme)?.setIcon(
            if (isNightModeActive()) R.drawable.ic_light_mode_white_24dp else R.drawable.ic_dark_mode_white_24dp
        )
        return true
    }

    private fun isNightModeActive(): Boolean =
        (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES

    /** PT → EN → ES → PT… (o AndroidX salva a escolha e recria as telas no novo idioma). */
    private fun switchToNextLanguage(languages: List<String>, current: String) {
        val next = languages[(languages.indexOf(current).coerceAtLeast(0) + 1) % languages.size]
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
            androidx.core.os.LocaleListCompat.forLanguageTags(next)
        )
    }

    /** Alterna entre claro e escuro a partir do que está na tela agora (sai do "padrão do sistema"). */
    private fun toggleTheme() {
        settingsManager.setThemeMode(
            if (isNightModeActive()) SettingsManager.THEME_LIGHT else SettingsManager.THEME_DARK
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_theme -> toggleTheme()
            R.id.action_settings -> openSettings()
            R.id.action_glossary -> openGlossary()
            R.id.action_about -> openAbout()
            R.id.action_references -> openReferences()
            R.id.action_support -> Supporter.openSupportScreen(this, "menu")
            else -> return false
        }

        return true
    }

    override fun setupContentView() {
        setupToolbar(binding.toolbar, displayHomeAsUpEnabled = false, displayShowTitleEnabled = false)
        setupHeroHeight()
        setupLinks()

        binding.heroSecondaryButton.setOnClickListener { openAllEras() }
        binding.erasSeeAll.setOnClickListener { openAllEras() }
    }

    /** O destaque ocupa boa parte da tela, mas sem esconder todo o conteúdo em telas pequenas. */
    private fun setupHeroHeight() {
        val minHeight = resources.getDimensionPixelSize(R.dimen.home_hero_min_height)
        val maxHeight = resources.getDimensionPixelSize(R.dimen.home_hero_max_height)
        val screenHeight = resources.displayMetrics.heightPixels
        val height = (screenHeight * 0.66f).toInt().coerceIn(minHeight, maxHeight)

        binding.hero.layoutParams = binding.hero.layoutParams.apply { this.height = height }
    }

    private fun setupLinks() {
        bindLink(binding.glossaryRow, R.drawable.ic_library_books, R.string.action_glossary, R.string.glossary_description) { openGlossary() }
        bindLink(binding.referencesRow, R.drawable.ic_open_book_white_24dp, R.string.action_reference, R.string.references_bibliographic_description) { openReferences() }
        bindLink(binding.settingsRow, R.drawable.ic_settings_white_24dp, R.string.action_settings, R.string.home_settings_description) { openSettings() }
        bindLink(binding.aboutRow, R.drawable.ic_info_white_24dp, R.string.action_about, R.string.home_about_description) { openAbout() }
    }

    private fun bindLink(row: ViewHomeLinkRowBinding, icon: Int, title: Int, description: Int, onClick: () -> Unit) {
        row.linkIcon.setImageResource(icon)
        row.linkTitle.setText(title)
        row.linkDescription.setText(description)
        row.root.setOnClickListener { onClick() }
    }

    override fun bindHomeContent(homeContentList: List<HomeContentItem>, historyEras: List<MainTopicItem>) {
        this.historyEras = historyEras
        this.historyDarkMode = homeContentList
            .firstOrNull { it.mainTopicType == MainTopicsType.HISTORY_CINEMA }
            ?.darkMode ?: true

        bindHero(fallbackImage = homeContentList.firstOrNull { it.mainTopicType == MainTopicsType.HISTORY_CINEMA })
        bindEras()
        bindDestinations(homeContentList.filter { it.mainTopicType != MainTopicsType.HISTORY_CINEMA })
    }

    // ---------------------------------------------------------------- Destaque (História do Cinema)

    private fun bindHero(fallbackImage: HomeContentItem? = null) {
        val lastReadId = settingsManager.getLastReadEraId()
        val continueEra = historyEras.firstOrNull { it.id == lastReadId && !it.blocked }
        val era = continueEra ?: historyEras.lastOrNull { !it.blocked }

        if (era == null) {
            bindHeroWithoutEras(fallbackImage)
            return
        }

        val changed = heroEra?.id != era.id
        heroEra = era

        binding.heroKicker.setText(
            when {
                continueEra != null -> R.string.home_continue_reading
                else -> R.string.home_latest_era
            }
        )
        binding.heroPrimaryButton.setText(
            if (continueEra != null) R.string.home_continue_button else R.string.home_start_reading
        )
        binding.heroTitle.text = era.title
        binding.heroDescription.text = era.description
        binding.heroPart.text = era.subtitle
        binding.heroPart.show()
        binding.heroPart.backgroundTintList = ColorStateList.valueOf(eraColor(era))

        if (changed) {
            binding.heroImage.loadImage(HomeImages.eraCover(era), placeholder = null)
            bindQuote(era)
        }

        binding.hero.setOnClickListener { openEra(era) }
        binding.heroPrimaryButton.setOnClickListener { openEra(era) }
    }

    /** Sem as eras (erro ao carregar maintopics.json): o destaque leva à lista da História do Cinema. */
    private fun bindHeroWithoutEras(homeContent: HomeContentItem?) {
        homeContent?.let { binding.heroImage.loadImage(HomeImages.coverOf(it.image), placeholder = null) }

        binding.heroPart.hide()
        binding.heroKicker.hide()
        binding.heroTitle.setText(R.string.history_cinema_title)
        binding.heroDescription.hide()
        binding.heroPrimaryButton.setText(R.string.start_journey)
        binding.heroSecondaryButton.hide()

        binding.hero.setOnClickListener { openAllEras() }
        binding.heroPrimaryButton.setOnClickListener { openAllEras() }
    }

    private fun bindQuote(era: MainTopicItem) {
        @Suppress("SENSELESS_COMPARISON") // o Gson pode deixar campos não anuláveis como null
        val quote = era.quote.takeIf { it != null && !it.quote.isNullOrBlank() }

        if (quote == null) {
            binding.quoteCard.hide()
            return
        }

        binding.quoteText.text = quote.quote
        binding.quoteAuthor.text = quote.author
        binding.quoteIcon.imageTintList = ColorStateList.valueOf(eraColor(era))
        binding.quoteCard.show()
    }

    // ---------------------------------------------------------------- Eras

    private fun bindEras() {
        if (historyEras.isEmpty()) {
            binding.erasHeader.hide()
            binding.erasCarousel.hide()
            return
        }

        binding.erasSubtitle.text = getString(R.string.home_section_eras_subtitle, historyEras.size)
        binding.erasCarousel.adapter = HomeErasAdapter(historyEras).apply {
            onEraClicked = { openEra(it) }
        }
    }

    // ---------------------------------------------------------------- Outras áreas

    private fun bindDestinations(destinations: List<HomeContentItem>) {
        binding.destinationsList.layoutManager = GridLayoutManager(this, 2)
        binding.destinationsList.adapter = HomeDestinationsAdapter(destinations).apply {
            onDestinationClicked = { openDestination(it) }
        }
    }

    // ---------------------------------------------------------------- Navegação

    private fun openEra(era: MainTopicItem) {
        startActivityWithSlideRightToLeftAnimation(PresentationActivity.newInstance(this, era))
    }

    private fun openAllEras() {
        startActivityWithSlideRightToLeftAnimation(
            MainTopicsActivity.newIntent(MainTopicsType.HISTORY_CINEMA, this, darkMode = historyDarkMode)
        )
    }

    private fun openDestination(homeContentItem: HomeContentItem) {
        if (homeContentItem.mainTopicType == MainTopicsType.TIMELINE) {
            startActivityWithSlideRightToLeftAnimation(TimelineActivity.newIntent(this))
            return
        }

        startActivityWithSlideRightToLeftAnimation(
            MainTopicsActivity.newIntent(
                homeContentItem.mainTopicType,
                this,
                darkMode = homeContentItem.darkMode
            )
        )
    }

    private fun openSettings() = startActivityWithSlideRightToLeftAnimation(SettingActivity.newIntent(this))
    private fun openGlossary() = startActivityWithSlideRightToLeftAnimation(GlossaryActivity.newIntent(this))
    private fun openAbout() = startActivityWithSlideRightToLeftAnimation(AboutActivty.newIntent(this))
    private fun openReferences() = startActivityWithSlideRightToLeftAnimation(ReferenceActivity.newIntent(this))

    private fun eraColor(era: MainTopicItem): Int =
        HomeImages.colorByName(this, era.color, ContextCompat.getColor(this, R.color.md_black_1000))

    // ---------------------------------------------------------------- Loading

    override fun startLoading() {
        binding.loadView.visibility = View.VISIBLE
        binding.loadView.startShimmer()
    }

    override fun hideLoading() {
        binding.loadView.stopShimmer()
        binding.loadView.visibility = View.GONE

        listOf(binding.appBar, binding.homeScroll).forEach {
            it.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator(2f))
                .start()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}
