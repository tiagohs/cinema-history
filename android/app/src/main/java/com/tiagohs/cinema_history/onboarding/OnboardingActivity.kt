package com.tiagohs.cinema_history.onboarding

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.ActivityOnboardingBinding
import com.tiagohs.cinema_history.databinding.ItemOnboardingPageBinding
import com.tiagohs.cinema_history.databinding.ItemSupportBenefitBinding
import com.tiagohs.cinema_history.databinding.ViewOnboardingListBinding
import com.tiagohs.cinema_history.databinding.ViewOnboardingPreferencesBinding
import com.tiagohs.cinema_history.presentation.activities.HomeActivity
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.Motion
import com.tiagohs.cinema_history.support.Supporter
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.utils.ContentLanguage
import kotlin.math.abs

/**
 * Onboarding da primeira abertura (depois da splash, antes da Home). A Home só pede o consentimento
 * de anúncios (UMP) depois que este fluxo termina, porque ela só abre depois dele.
 *
 * Páginas: 1) 130 anos de cinema + idioma e tema; 2) muito além da história; 3) cultura é gratuita;
 * 4) quer ajudar? — só quando a oferta de apoio existe (Brasil + português).
 */
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>() {

    override fun inflateBinding(inflater: LayoutInflater) = ActivityOnboardingBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    private lateinit var adapter: PagesAdapter
    private val dots = mutableListOf<View>()

    private val settingsManager by lazy {
        SettingsManager(
            applicationContext,
            getSharedPreferences(Constants.SHARED_PREFERENCES.PREF_SETTINGS_NAME, Constants.SHARED_PREFERENCES.PRIVATE_MODE)
        )
    }

    /** A oferta pode ficar disponível alguns instantes depois (país vem do Google Play). */
    private val supporterListener: () -> Unit = { updatePages() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInsets()

        adapter = PagesAdapter(buildPages())
        binding.onboardingPager.adapter = adapter
        binding.onboardingPager.offscreenPageLimit = 1
        if (Motion.enabled(this)) binding.onboardingPager.setPageTransformer(ParallaxTransformer())

        binding.onboardingPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateControls(position)
        })

        binding.onboardingSkip.setOnClickListener { finishOnboarding(openSupport = false) }
        binding.onboardingNext.setOnClickListener { goNext() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val current = binding.onboardingPager.currentItem
                if (current > 0) {
                    binding.onboardingPager.currentItem = current - 1
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        buildDots()
        updateControls(binding.onboardingPager.currentItem)

        Supporter.addListener(supporterListener)
    }

    override fun onDestroy() {
        Supporter.removeListener(supporterListener)
        super.onDestroy()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.onboardingTopBar) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()).top
            view.updatePadding(top = top + (8 * resources.displayMetrics.density).toInt())
            insets
        }
    }

    // ------------------------------------------------------------------ Páginas

    private fun buildPages(): List<Page> = buildList {
        add(Page.WELCOME)
        add(Page.BEYOND)
        add(Page.FREE)
        if (Supporter.isOfferAvailable(this@OnboardingActivity)) add(Page.SUPPORT)
    }

    private fun updatePages() {
        val pages = buildPages()
        if (pages == adapter.pages) return
        adapter.update(pages)
        buildDots()
        updateControls(binding.onboardingPager.currentItem)
    }

    private fun goNext() {
        val next = binding.onboardingPager.currentItem + 1
        if (next < adapter.itemCount) {
            binding.onboardingPager.currentItem = next
        } else {
            finishOnboarding(openSupport = false)
        }
    }

    private fun updateControls(position: Int) {
        val isLast = position == adapter.itemCount - 1
        val page = adapter.pages.getOrNull(position)

        // A página de apoio tem os próprios botões ("Conhecer o apoio" / "Agora não").
        binding.onboardingNext.isVisible = page != Page.SUPPORT
        binding.onboardingNext.setText(if (isLast) R.string.onboarding_start else R.string.onboarding_next)
        binding.onboardingNext.setIconResource(if (isLast) 0 else R.drawable.ic_navigate_next_black_24dp)

        dots.forEachIndexed { index, dot -> animateDot(dot, index == position) }
    }

    private fun buildDots() {
        val container = binding.onboardingDots
        container.removeAllViews()
        dots.clear()
        val density = resources.displayMetrics.density
        repeat(adapter.itemCount) {
            val dot = View(this).apply {
                setBackgroundResource(R.drawable.background_onboarding_dot)
                layoutParams = LinearLayout.LayoutParams((8 * density).toInt(), (8 * density).toInt()).apply {
                    marginEnd = (8 * density).toInt()
                }
                alpha = 0.4f
            }
            container.addView(dot)
            dots += dot
        }
    }

    private fun animateDot(dot: View, selected: Boolean) {
        val density = resources.displayMetrics.density
        val target = ((if (selected) 24 else 8) * density).toInt()
        val params = dot.layoutParams
        dot.animate().alpha(if (selected) 1f else 0.4f).setDuration(200).start()
        if (params.width == target) return
        if (!Motion.enabled(this)) {
            params.width = target
            dot.layoutParams = params
            return
        }
        ValueAnimator.ofInt(params.width, target).apply {
            duration = 220
            addUpdateListener {
                params.width = it.animatedValue as Int
                dot.layoutParams = params
            }
        }.start()
    }

    // ------------------------------------------------------------------ Saída

    private fun finishOnboarding(openSupport: Boolean) {
        Onboarding.markDone(this)
        startActivity(HomeActivity.newIntent(this))
        if (openSupport) Supporter.openSupportScreen(this, SOURCE)
        finish()
    }

    // ------------------------------------------------------------------ Idioma e tema (página 1)

    private fun bindPreferences(view: ViewOnboardingPreferencesBinding) {
        val languages = ContentLanguage.ENABLED
        val buttons = mapOf(
            ContentLanguage.PORTUGUESE to view.languagePt,
            ContentLanguage.ENGLISH to view.languageEn,
            ContentLanguage.SPANISH to view.languageEs
        )
        view.languageSection.isVisible = languages.size >= 2
        buttons.forEach { (language, button) -> button.isVisible = language in languages }
        buttons[ContentLanguage.current()]?.let { view.languageGroup.check(it.id) }
        view.languageGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val language = buttons.entries.firstOrNull { it.value.id == checkedId }?.key ?: return@addOnButtonCheckedListener
            if (language == ContentLanguage.current()) return@addOnButtonCheckedListener
            // Recria as telas no novo idioma; a escolha é salva pelo AndroidX (autoStoreLocales).
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        }

        val themes = mapOf(
            SettingsManager.THEME_SYSTEM to view.themeSystem,
            SettingsManager.THEME_LIGHT to view.themeLight,
            SettingsManager.THEME_DARK to view.themeDark
        )
        themes[settingsManager.getThemeMode()]?.let { view.themeGroup.check(it.id) }
        view.themeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val mode = themes.entries.firstOrNull { it.value.id == checkedId }?.key ?: return@addOnButtonCheckedListener
            if (mode != settingsManager.getThemeMode()) settingsManager.setThemeMode(mode)
        }
    }

    // ------------------------------------------------------------------ Listas (páginas 2, 3 e 4)

    private fun addRow(container: ViewGroup, icon: Int, title: Int, subtitle: Int) {
        val row = ItemSupportBenefitBinding.inflate(layoutInflater, container, false)
        row.benefitIcon.setImageResource(icon)
        row.benefitIcon.backgroundTintList = ColorStateList.valueOf(0x26FFFFFF)
        row.benefitIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.oscar))
        row.benefitTitle.setText(title)
        row.benefitTitle.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000))
        row.benefitSubtitle.setText(subtitle)
        row.benefitSubtitle.setTextColor(ContextCompat.getColor(this, R.color.home_text_on_image_secondary))
        container.addView(row.root)
    }

    private fun bindBeyond(view: ViewOnboardingListBinding) {
        addRow(view.listItems, R.drawable.ic_support_trophy_24, R.string.onboarding_2_awards, R.string.onboarding_2_awards_subtitle)
        addRow(view.listItems, R.drawable.ic_support_timeline_24, R.string.onboarding_2_timeline, R.string.onboarding_2_timeline_subtitle)
        addRow(view.listItems, R.drawable.ic_support_movie_24, R.string.onboarding_2_movies, R.string.onboarding_2_movies_subtitle)
        addRow(view.listItems, R.drawable.ic_support_person_24, R.string.onboarding_2_directors, R.string.onboarding_2_directors_subtitle)
    }

    private fun bindFree(view: ViewOnboardingListBinding) {
        view.listBadge.isVisible = true
        view.listBadge.setText(R.string.onboarding_3_badge)
        view.listBadge.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_support_heart_24, 0, 0, 0)
        view.listBadge.compoundDrawablesRelative.firstOrNull()?.setTint(ContextCompat.getColor(this, R.color.md_black_1000))
    }

    private fun bindSupport(view: ViewOnboardingListBinding) {
        addRow(view.listItems, R.drawable.ic_support_headphones_24, R.string.support_benefit_audio_title, R.string.support_benefit_audio_subtitle)
        addRow(view.listItems, R.drawable.ic_support_no_ads_24, R.string.support_benefit_ads_title, R.string.support_benefit_ads_subtitle)
        addRow(view.listItems, R.drawable.ic_support_edit_24, R.string.support_benefit_people_title, R.string.support_benefit_people_subtitle)
        view.listActions.isVisible = true
        view.listPrimary.setOnClickListener { finishOnboarding(openSupport = true) }
        view.listSecondary.setOnClickListener { finishOnboarding(openSupport = false) }
    }

    // ------------------------------------------------------------------ Adapter

    enum class Page(val image: Int, val kicker: Int, val title: Int, val text: Int) {
        WELCOME(R.drawable.img_voyage, R.string.onboarding_1_kicker, R.string.onboarding_1_title, R.string.onboarding_1_text),
        BEYOND(R.drawable.img_awards, R.string.onboarding_2_kicker, R.string.onboarding_2_title, R.string.onboarding_2_text),
        FREE(R.drawable.img_singin_in_the_rain, R.string.onboarding_3_kicker, R.string.onboarding_3_title, R.string.onboarding_3_text),
        SUPPORT(R.drawable.img_the_gold_rush_2, R.string.onboarding_4_kicker, R.string.onboarding_4_title, R.string.onboarding_4_text)
    }

    private inner class PagesAdapter(pages: List<Page>) : RecyclerView.Adapter<PageHolder>() {

        var pages: List<Page> = pages
            private set

        fun update(newPages: List<Page>) {
            val old = pages
            pages = newPages
            // Só acrescenta/remove a última página (apoio); as outras não mudam.
            when {
                newPages.size > old.size -> notifyItemRangeInserted(old.size, newPages.size - old.size)
                newPages.size < old.size -> notifyItemRangeRemoved(newPages.size, old.size - newPages.size)
                else -> notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = pages.size
        override fun getItemViewType(position: Int): Int = pages[position].ordinal

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
            val page = Page.entries[viewType]
            val binding = ItemOnboardingPageBinding.inflate(layoutInflater, parent, false)
            binding.pageImage.setImageResource(page.image)
            binding.pageKicker.setText(page.kicker)
            binding.pageTitle.setText(page.title)
            binding.pageText.setText(page.text)

            when (page) {
                Page.WELCOME -> bindPreferences(ViewOnboardingPreferencesBinding.inflate(layoutInflater, binding.pageExtra, true))
                Page.BEYOND -> bindBeyond(ViewOnboardingListBinding.inflate(layoutInflater, binding.pageExtra, true))
                Page.FREE -> bindFree(ViewOnboardingListBinding.inflate(layoutInflater, binding.pageExtra, true))
                Page.SUPPORT -> bindSupport(ViewOnboardingListBinding.inflate(layoutInflater, binding.pageExtra, true))
            }
            return PageHolder(binding)
        }

        override fun onBindViewHolder(holder: PageHolder, position: Int) = Unit
    }

    private class PageHolder(val binding: ItemOnboardingPageBinding) : RecyclerView.ViewHolder(binding.root)

    /** Animação discreta entre páginas: a imagem anda mais devagar (parallax) e o texto esmaece. */
    private class ParallaxTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val image = page.findViewById<View>(R.id.pageImage) ?: return
            val content = page.findViewById<View>(R.id.pageContent) ?: return
            val width = page.width
            when {
                position <= -1f || position >= 1f -> {
                    image.translationX = 0f
                    content.alpha = 1f
                    content.translationX = 0f
                }
                else -> {
                    image.translationX = -position * width * 0.5f
                    content.alpha = (1f - abs(position) * 1.6f).coerceIn(0f, 1f)
                    content.translationX = position * width * 0.15f
                }
            }
        }
    }

    companion object {
        const val SOURCE = "onboarding"

        fun newIntent(context: Context): Intent = Intent(context, OnboardingActivity::class.java)
    }
}
