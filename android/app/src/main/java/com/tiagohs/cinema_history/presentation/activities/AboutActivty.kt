package com.tiagohs.cinema_history.presentation.activities

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.support.SupportDebug
import com.tiagohs.cinema_history.support.Supporter
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.extensions.isDarkThemeActive
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*

class AboutActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tablets: créditos e links numa coluna centralizada (o fundo continua de ponta a ponta).
        setContentView(setupContentView().apply { limitContentWidth(R.dimen.ls_form_max_width) })
        com.tiagohs.helpers.edgetoedge.SystemBarsInsets.apply(this)

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getResourceString(R.string.activity_about)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun setupContentView(): View {
        val adsElement = Element().apply {
            title = getResourceString(R.string.responsable)
        }
        val termsElement = Element().apply {
            title = getResourceString(R.string.terms)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.terms_link)) }
        }
        val politicsElement = Element().apply {
            title = getResourceString(R.string.privacy)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.privacy_link)) }
        }
        val commonsCreativeElement = Element().apply {
            title = getResourceString(R.string.creative_commons)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.creative_commons_link)) }
        }
        // Aviso obrigatório do TMDB (sempre em inglês, texto exato).
        val tmdbNoticeElement = Element().apply {
            title = getResourceString(R.string.tmdb_notice)
        }
        val tmdbCreditsElement = Element().apply {
            title = getResourceString(R.string.credits_tmdb)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.tmdb_terms_link)) }
        }
        val omdbCreditsElement = Element().apply {
            title = getResourceString(R.string.credits_omdb)
        }
        val awardsCreditsElement = Element().apply {
            title = getResourceString(R.string.credits_awards)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.credits_awards_link)) }
        }
        val youtubeCreditsElement = Element().apply {
            title = getResourceString(R.string.credits_youtube)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.credits_youtube_link)) }
        }
        val freeCultureElement = Element().apply {
            title = getResourceString(R.string.about_free_culture)
            gravity = Gravity.CENTER
        }
        val tmdbTermesLinkElement = Element().apply {
            title = getResourceString(R.string.tmdb_terms_title)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.tmdb_terms_link)) }
        }
        val tmdbApiTermesLinkElement = Element().apply {
            title = getResourceString(R.string.tmdb_api_terms_title)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.tmdb_api_terms_link)) }
        }
        val omdbApiTermesLinkElement = Element().apply {
            title = getResourceString(R.string.omdb_api_terms_title)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.omdb_api_terms_link)) }
        }
        val referencesElement = Element().apply {
            title = getResourceString(R.string.activity_references)
            onClickListener = View.OnClickListener {
                startActivityWithSlideRightToLeftAnimation(ReferenceActivity.newIntent(this@AboutActivty))
            }
        }
        val glossaryElement = Element().apply {
            title = getResourceString(R.string.action_glossary)
            onClickListener = View.OnClickListener {
                startActivityWithSlideRightToLeftAnimation(GlossaryActivity.newIntent(this@AboutActivty))
            }
        }
        val copyRightsElement = Element().apply {
            val copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR))

            title = copyrights
            iconDrawable = R.drawable.ic_about_copyrights
            autoApplyIconTint = true
            iconTint = R.color.about_item_icon_color
            iconNightTint = android.R.color.white
            gravity = Gravity.CENTER
            onClickListener = View.OnClickListener {
                Toast.makeText(this@AboutActivty, copyrights, Toast.LENGTH_SHORT).show()
            }
        }

        // Em builds de debug, 7 toques na versão abrem as opções de teste do apoio (SupportDebug).
        val versionElement = Element().apply {
            title = getString(R.string.version, BuildConfig.VERSION_NAME)
            if (BuildConfig.DEBUG) {
                onClickListener = View.OnClickListener { SupportDebug.onVersionTapped(this@AboutActivty) }
            }
        }

        val page = AboutPage(this)
            .isRTL(false)
            .enableDarkMode(isDarkThemeActive()) // segue o tema do app (Configurações)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.app_description, getResourceString(R.string.app_name)))
            .addItem(freeCultureElement)
            .addItem(versionElement)
            .addItem(adsElement)
            .addItem(referencesElement)
            .addItem(glossaryElement)
            .apply { supportElements().forEach { addItem(it) } }
            .addEmail(getResourceString(R.string.email), getResourceString(R.string.contact_us))
            .addItem(termsElement)
            .addItem(politicsElement)
            .addGroup(getResourceString(R.string.credits_terms))
            .addItem(tmdbNoticeElement)
            .addItem(tmdbCreditsElement)
            .addItem(tmdbTermesLinkElement)
            .addItem(tmdbApiTermesLinkElement)
            .addItem(omdbCreditsElement)
            .addItem(omdbApiTermesLinkElement)
            .addItem(awardsCreditsElement)
            .addItem(youtubeCreditsElement)
            .addItem(commonsCreativeElement)
            .addItem(copyRightsElement)

        return page.create()
    }

    /**
     * "Apoie o app" + agradecimento a quem apoia (só onde a oferta existe ou para quem já apoia).
     * Apoiador: só o agradecimento pessoal; com oferta e sem apoio: a entrada "Apoie o app" e o
     * agradecimento a todos os apoiadores. Ambos abrem a tela de apoio.
     */
    private fun supportElements(): List<Element> {
        val supporter = Supporter.isSupporter(this)
        if (!supporter && !Supporter.isOfferAvailable(this)) return emptyList()

        fun element(titleRes: Int) = Element().apply {
            title = getString(titleRes)
            iconDrawable = R.drawable.ic_support_heart_24
            autoApplyIconTint = true
            iconTint = R.color.about_item_icon_color
            iconNightTint = android.R.color.white
            onClickListener = View.OnClickListener { Supporter.openSupportScreen(this@AboutActivty, "about") }
        }

        return if (supporter) {
            listOf(element(R.string.support_about_supporter))
        } else {
            listOf(element(R.string.support_menu), element(R.string.support_about_thanks))
        }
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, AboutActivty::class.java)
    }
}