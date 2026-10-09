package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.ActivityAwardDetailsBinding
import com.tiagohs.cinema_history.presentation.adapters.AwardPagerAdapter
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardActions
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.Motion
import com.tiagohs.cinema_history.presentation.fragments.AwardScreenHost
import com.tiagohs.cinema_history.presentation.fragments.AwardYearListener
import com.tiagohs.cinema_history.presentation.views.Images
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.presenter.AwardPresenter
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.awards.AwardHighlight
import com.tiagohs.entities.awards.AwardYearSummary
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.enums.NomineeType
import com.tiagohs.entities.enums.Screen
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.shareContent
import com.tiagohs.helpers.extensions.show
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import jp.wasabeef.glide.transformations.BlurTransformation
import javax.inject.Inject

/**
 * Tela de um prêmio, no estilo dos apps de streaming:
 * - hero do ano selecionado com o vencedor principal (backdrop ou pôster desfocado + pôster com
 *   borda dourada), que troca com crossfade e textos animados ao mudar de ano;
 * - parallax do hero e toolbar que ganha fundo conforme a tela rola;
 * - abas: indicados/vencedores (ano a ano, carregados sob demanda) e "Sobre".
 */
class AwardActivity : BaseActivity<ActivityAwardDetailsBinding>(), AwardView, AwardScreenHost, AwardActions {

    @Inject
    lateinit var presenter: AwardPresenter

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    private var awardMainTopic: AwardMainTopic? = null
    private var awardPagerAdapter: AwardPagerAdapter? = null

    private var selectedSummary: AwardYearSummary? = null
    private var restoredYear: String? = null
    private var yearState = YearState.IDLE
    private var yearResult: NomineeResult? = null
    private var yearListener: AwardYearListener? = null

    private var motionEnabled = true
    private var lastScrollFraction = -1f

    // Hero: duas camadas de imagem para o crossfade entre anos.
    private lateinit var heroFront: ImageView
    private lateinit var heroBack: ImageView
    private var heroModel: Any? = null
    private var heroToken = 0

    override val awardActions: AwardActions get() = this

    override fun inflateBinding(inflater: LayoutInflater) = ActivityAwardDetailsBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = R.menu.menu_award

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        setupToolbar(binding.toolbar)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        motionEnabled = Motion.enabled(this)
        restoredYear = savedInstanceState?.getString(STATE_YEAR)
        heroFront = binding.heroImageFront
        heroBack = binding.heroImageBack

        setupScrollEffects()

        presenter.onBindView(this)
        presenter.fetchAwardsNominees(awardMainTopic)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_YEAR, selectedSummary?.year)
    }

    override fun onDestroy() {
        presenter.onUnbindView()
        yearListener = null

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_share -> {
                onShareClicked()
                return true
            }
            else -> return false
        }

    }

    private fun onShareClicked() {
        showScreenBlocked()
        val id = awardMainTopic?.id ?: return

        dynamicLinkManager.buildAwardPageLink(
            id,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
        shareContent(
            getString(R.string.share_history_page_description, awardMainTopic?.name, shorLink),
            getResourceString(
                R.string.share_title
            )
        )

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    // ------------------------------------------------------------------ AwardView

    @Suppress("DEPRECATION")
    override fun setupArguments() {
        awardMainTopic = intent.extras?.getSerializable(MAIN_TOPIC) as? AwardMainTopic
    }

    override fun bindAwardsNomineesContent(awardMainTopic: AwardMainTopic) {
        this.awardMainTopic = awardMainTopic

        setupAwardIdentity(awardMainTopic)
        setupTabs()

        val index = awardMainTopic.yearIndex.orEmpty()
        val initial = index.firstOrNull { it.year == restoredYear } ?: index.firstOrNull()
        if (initial != null) {
            selectYear(initial)
        } else {
            showHero(null, animate = false, direction = 1)
        }
    }

    override fun bindAwardYear(year: String, result: NomineeResult) {
        if (year != selectedSummary?.year) return

        yearState = YearState.LOADED
        yearResult = result
        yearListener?.onYearLoaded(year, result)
    }

    override fun onAwardYearError(year: String, error: Throwable) {
        if (year != selectedSummary?.year) return

        yearState = YearState.FAILED
        yearResult = null
        yearListener?.onYearFailed(year)
    }

    override fun startLoading() {
        binding.appBar.alpha = 0f
        binding.viewPager.alpha = 0f
        binding.loadView.show()
    }

    override fun hideLoading() {
        listOf(binding.appBar, binding.viewPager).forEach {
            it.animate()
                .alpha(1f)
                .setDuration(if (motionEnabled) 250 else 0)
                .setInterpolator(DECELERATE)
                .start()
        }
        binding.loadView.hide()
    }

    // ------------------------------------------------------------------ AwardScreenHost

    override fun selectedYear(): String? = selectedSummary?.year

    override fun selectYear(summary: AwardYearSummary) {
        val previous = selectedSummary
        if (previous?.year == summary.year && yearState != YearState.FAILED) return

        selectedSummary = summary
        val direction = if ((summary.year.toIntOrNull() ?: 0) >= (previous?.year?.toIntOrNull() ?: 0)) 1 else -1
        showHero(summary, animate = motionEnabled && previous != null, direction = direction)
        loadSelectedYear()
    }

    override fun retrySelectedYear() {
        loadSelectedYear()
    }

    override fun setYearListener(listener: AwardYearListener?) {
        yearListener = listener
        val year = selectedSummary?.year ?: return
        listener ?: return

        // Repassa o estado atual para a aba recém-criada (ex.: depois de girar a tela).
        when (yearState) {
            YearState.LOADING -> listener.onYearLoading(year)
            YearState.LOADED -> yearResult?.let {
                listener.onYearLoading(year)
                listener.onYearLoaded(year, it)
            }
            YearState.FAILED -> listener.onYearFailed(year)
            YearState.IDLE -> {}
        }
    }

    private fun loadSelectedYear() {
        val year = selectedSummary?.year ?: return
        val awardId = awardMainTopic?.id ?: return

        yearState = YearState.LOADING
        yearResult = null
        yearListener?.onYearLoading(year)
        presenter.fetchAwardYear(awardId, year)
    }

    // ------------------------------------------------------------------ Hero

    private fun setupAwardIdentity(award: AwardMainTopic) {
        binding.awardLabel.text = award.name
        Images.model(this, award.logo)?.let {
            val size = resources.getDimensionPixelSize(R.dimen.awards_logo_size)
            Images.load(Glide.with(this), binding.awardLogo, it, size, size, crossFade = false)
        }
        binding.heroDetailsButton.setOnClickListener { openHighlight(it) }
        binding.heroPosterCard.setOnClickListener { openHighlight(it) }
    }

    private fun showHero(summary: AwardYearSummary?, animate: Boolean, direction: Int) {
        val award = awardMainTopic ?: return
        val highlight = summary?.highlight

        val title = highlight?.name ?: award.name
        val kicker = highlight?.category?.let { getString(R.string.award_winner_of, it) }
            ?: getString(R.string.award_hero_fallback_kicker)
        val counts = summary?.let {
            val categories = it.categories
            val nominees = it.nominees
            if (categories != null && nominees != null) getString(R.string.award_year_meta, categories, nominees) else null
        }
        val meta = listOfNotNull(highlight?.director?.takeIf { it.isNotBlank() }, counts).joinToString(" · ")

        setHeroText(binding.heroYear, summary?.year.orEmpty(), animate, direction, 0)
        setHeroText(binding.heroTitle, title.orEmpty(), animate, direction, 1)
        setHeroText(binding.heroWinnerLabel, kicker, animate, direction, 2)
        setHeroText(binding.heroMeta, meta, animate, direction, 3)
        binding.heroMeta.visibility = if (meta.isBlank()) View.GONE else View.VISIBLE
        binding.heroWinnerLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (highlight != null) R.drawable.ic_awards_trophy_16dp else 0, 0, 0, 0
        )
        binding.heroDetailsButton.visibility = if (highlight?.id != null) View.VISIBLE else View.GONE
        binding.heroPosterCard.contentDescription = title
        binding.toolbarTitle.text = listOfNotNull(award.name, summary?.year).joinToString(" · ")

        // Pôster do vencedor (cartão com borda dourada).
        val posterUrl = posterUrlOf(highlight)
        if (posterUrl != null) {
            binding.heroPosterCard.visibility = View.VISIBLE
            val width = resources.getDimensionPixelSize(R.dimen.awards_hero_poster_width)
            val height = resources.getDimensionPixelSize(R.dimen.awards_hero_poster_height)
            Images.load(Glide.with(this), binding.heroPoster, posterUrl, width, height, crossFade = motionEnabled)
            if (animate) {
                binding.heroPosterCard.scaleX = 0.9f
                binding.heroPosterCard.scaleY = 0.9f
                binding.heroPosterCard.animate().scaleX(1f).scaleY(1f).setDuration(420).setInterpolator(DECELERATE).start()
            }
        } else {
            binding.heroPosterCard.visibility = View.GONE
        }

        // Fundo: backdrop do vencedor; sem backdrop, o próprio pôster desfocado; sem vencedor, a imagem do prêmio.
        val backdrop = highlight?.backdropPath?.let {
            Images.tmdb(it, if (resources.displayMetrics.widthPixels > 900) ImageSize.BACKDROP_1280 else ImageSize.BACKDROP_780)
        }
        val background: Any? = backdrop
            ?: posterUrlOf(highlight, ImageSize.POSTER_185)
            ?: Images.model(this, award.image)
        loadHeroImage(background, blur = backdrop == null, animate = animate)
    }

    private fun posterUrlOf(highlight: AwardHighlight?, movieSize: ImageSize = ImageSize.POSTER_342): String? {
        highlight ?: return null
        val size = if (highlight.type == NomineeType.PERSON) ImageSize.PROFILE_632 else movieSize
        return Images.tmdb(highlight.imagePath, size)
    }

    /** Odômetro simples: o texto antigo sai para cima, o novo entra de baixo (ou o contrário, voltando no tempo). */
    private fun setHeroText(view: TextView, text: String, animate: Boolean, direction: Int, order: Int) {
        if (!animate || view.text.toString() == text) {
            view.animate().cancel()
            view.alpha = 1f
            view.translationY = 0f
            view.text = text
            return
        }
        val distance = resources.displayMetrics.density * 14 * direction
        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .translationY(-distance)
            .setStartDelay(order * 35L)
            .setDuration(140)
            .setInterpolator(DECELERATE)
            .withEndAction {
                view.text = text
                view.translationY = distance
                view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(0)
                    .setDuration(320)
                    .setInterpolator(DECELERATE)
                    .start()
            }
            .start()
    }

    private fun loadHeroImage(model: Any?, blur: Boolean, animate: Boolean) {
        if (model == heroModel) return
        heroModel = model
        val token = ++heroToken
        val glide = Glide.with(this)

        if (model == null) {
            glide.clear(heroBack)
            return
        }

        // A camada de trás recebe a imagem nova; quando pronta, as duas trocam com crossfade.
        heroFront.animate().cancel()
        heroBack.animate().cancel()
        heroFront.alpha = 1f
        heroBack.alpha = 0f
        val target = heroBack

        val width = resources.displayMetrics.widthPixels
        val height = resources.getDimensionPixelSize(R.dimen.awards_hero_height)
        var request = glide.load(model)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        request = if (blur) {
            request.override(width / 4, height / 4).transform(CenterCrop(), BlurTransformation(12, 2))
        } else {
            request.override(width, height).centerCrop()
        }
        request
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean = false

                override fun onResourceReady(resource: Drawable, model: Any, t: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    target.post { if (token == heroToken) crossfadeHero(animate) }
                    return false
                }
            })
            .into(target)
    }

    private fun crossfadeHero(animate: Boolean) {
        val incoming = heroBack
        val outgoing = heroFront
        heroFront = incoming
        heroBack = outgoing
        val token = heroToken

        if (!animate || !motionEnabled) {
            incoming.alpha = 1f
            outgoing.alpha = 0f
            Glide.with(this).clear(outgoing)
            return
        }

        // "Ken Burns" curto: a imagem nova entra levemente ampliada e assenta.
        incoming.scaleX = 1.08f
        incoming.scaleY = 1.08f
        incoming.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(900).setInterpolator(DECELERATE).start()
        outgoing.animate().alpha(0f).setDuration(450).withEndAction {
            if (token == heroToken && !isDestroyed) Glide.with(this).clear(outgoing)
        }.start()
    }

    private fun openHighlight(from: View) {
        val highlight = selectedSummary?.highlight ?: return
        val id = highlight.id ?: return
        val intent = if (highlight.type == NomineeType.PERSON) PersonDetailsActivity.newIntent(this, id)
        else MovieDetailsActivity.newIntent(this, id)
        launch(intent, binding.heroPosterCard.takeIf { it.visibility == View.VISIBLE } ?: from)
    }

    // ------------------------------------------------------------------ Scroll (parallax + toolbar)

    private fun setupScrollEffects() {
        if (!motionEnabled) {
            // Sem parallax quando o sistema pede menos movimento.
            listOf(binding.heroImages, binding.heroContent).forEach {
                (it.layoutParams as CollapsingToolbarLayout.LayoutParams).parallaxMultiplier = 0f
            }
        }

        // O fundo da toolbar cobre também a área da status bar (o hero desenha atrás dela).
        binding.coordinator.doOnLayout {
            val statusBar = ViewCompat.getRootWindowInsets(it)
                ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
            val height = statusBar + binding.toolbar.height
            if (binding.toolbarBackground.layoutParams.height != height) {
                binding.toolbarBackground.layoutParams = binding.toolbarBackground.layoutParams.apply { this.height = height }
            }
        }

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            val range = appBar.totalScrollRange
            if (range <= 0) return@OnOffsetChangedListener
            val fraction = (-verticalOffset).toFloat() / range
            if (fraction == lastScrollFraction) return@OnOffsetChangedListener
            lastScrollFraction = fraction

            // Só propriedades de desenho (alpha/scale): nada de novo layout a cada frame.
            binding.toolbarBackground.alpha = ((fraction - 0.55f) / 0.35f).coerceIn(0f, 1f)
            binding.toolbarTitle.alpha = ((fraction - 0.8f) / 0.2f).coerceIn(0f, 1f)
            binding.heroContent.alpha = 1f - (fraction / 0.7f).coerceIn(0f, 1f)
            if (motionEnabled) {
                val scale = 1f + 0.1f * fraction
                binding.heroImages.scaleX = scale
                binding.heroImages.scaleY = scale
            }
        })
    }

    // ------------------------------------------------------------------ AwardActions (navegação)

    override fun onNomineeClicked(nominee: Nominee, sharedView: View) {
        val id = nominee.id
        val intent = when {
            nominee.type == NomineeType.PERSON && id != null -> PersonDetailsActivity.newIntent(this, id)
            nominee.type == NomineeType.MOVIE && id != null -> MovieDetailsActivity.newIntent(this, id)
            nominee.movie?.id != null -> MovieDetailsActivity.newIntent(this, nominee.movie?.id ?: return)
            else -> return
        }
        launch(intent, sharedView)
    }

    override fun onPersonClicked(personId: Int, sharedView: View) {
        launch(PersonDetailsActivity.newIntent(this, personId), sharedView)
    }

    override fun onVideoClicked(videoId: String) {
        openLink(getString(R.string.youtube_link, videoId))
    }

    override fun onLinkClicked(url: String?) {
        openLink(url)
    }

    override fun onBlockClicked(click: Click) {
        when (click.screen) {
            Screen.TIMELINE_SCREEN -> {
                val intent = TimelineActivity.newIntent(this).apply {
                    click.parameters?.forEach { parameter -> putExtra(parameter.key, parameter.value) }
                }
                startActivityWithSlideRightToLeftAnimation(intent)
            }
            Screen.LINK_ONLINE -> openLink(click.parameters?.firstOrNull()?.value)
            else -> {}
        }
    }

    /** Abre o detalhe "crescendo" a partir do pôster tocado (sem animação se o sistema pedir). */
    private fun launch(intent: Intent, from: View?) {
        if (from != null && motionEnabled && from.width > 0 && from.height > 0) {
            val options = ActivityOptionsCompat.makeScaleUpAnimation(from, 0, 0, from.width, from.height)
            ActivityCompat.startActivity(this, intent, options.toBundle())
            return
        }
        startActivityWithSlideRightToLeftAnimation(intent)
    }

    // ------------------------------------------------------------------ Tabs

    private fun setupTabs() {
        val awardMainTopic = awardMainTopic ?: return

        awardPagerAdapter = AwardPagerAdapter(this, awardMainTopic, supportFragmentManager)

        binding.viewPager.adapter = awardPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun showScreenBlocked() {
        binding.screenBlocked.root.show()
    }

    private fun hideScreenBlocked() {
        binding.screenBlocked.root.hide()
    }

    private enum class YearState { IDLE, LOADING, LOADED, FAILED }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"
        private const val STATE_YEAR = "STATE_YEAR"
        private val DECELERATE = DecelerateInterpolator(2f)

        fun newIntent(mainTopic: AwardMainTopic, context: Context): Intent {
            val intent = Intent(context, AwardActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}
