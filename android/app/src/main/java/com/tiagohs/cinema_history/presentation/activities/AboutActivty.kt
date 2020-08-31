package com.tiagohs.cinema_history.presentation.activities

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
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.extensions.openLink
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*

class AboutActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(setupContentView())

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
        val tmdbTermesDescriptionElement = Element().apply {
            title = getResourceString(R.string.tmdb_terms_description)
            onClickListener = View.OnClickListener { }
        }
        val tmdbTermesLinkElement = Element().apply {
            title = getResourceString(R.string.tmdb_terms_title)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.tmdb_terms_link)) }
        }
        val tmdbApiTermesLinkElement = Element().apply {
            title = getResourceString(R.string.tmdb_api_terms_title)
            onClickListener = View.OnClickListener { openLink(getResourceString(R.string.tmdb_api_terms_link)) }
        }
        val referencesElement = Element().apply {
            title = getResourceString(R.string.activity_references)
            onClickListener = View.OnClickListener {
                startActivity(ReferenceActivity.newIntent(this@AboutActivty))
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

        return AboutPage(this)
            .isRTL(false)
            .enableDarkMode(false)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.app_description, getResourceString(R.string.app_name)))
            .addItem(Element().setTitle(getString(R.string.version, BuildConfig.VERSION_NAME)))
            .addItem(adsElement)
            .addItem(referencesElement)
            .addEmail(getResourceString(R.string.email), getResourceString(R.string.contact_us))
            .addItem(termsElement)
            .addItem(politicsElement)
            .addGroup(getResourceString(R.string.credits_terms))
            .addItem(commonsCreativeElement)
            .addItem(tmdbTermesDescriptionElement)
            .addItem(tmdbTermesLinkElement)
            .addItem(tmdbApiTermesLinkElement)
            .addItem(copyRightsElement)
            .create()
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, AboutActivty::class.java)
    }
}