package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.lang.String
import java.util.*

class AboutActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(setupContentView())

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.activity_about)
    }

    private fun setupContentView(): View {
        val adsElement = Element().apply {
            title = getString(R.string.responsable)
        }
        val termsElement = Element().apply {
            title = getString(R.string.terms)
            onClickListener = View.OnClickListener {

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
            .setDescription(getString(R.string.app_description, getString(R.string.app_name)))
            .addItem(Element().setTitle(getString(R.string.version, BuildConfig.VERSION_NAME)))
            .addItem(adsElement)
            .addItem(termsElement)
            .addGroup(getString(R.string.sugestions))
            .addEmail("tiago.hsilva@gmail.com", getString(R.string.contact_us))
            .addItem(copyRightsElement)
            .create()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        fun newIntent(context: Context?): Intent = Intent(context, AboutActivty::class.java)
    }
}