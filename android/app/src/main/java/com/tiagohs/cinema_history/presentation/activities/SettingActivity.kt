package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.fragments.SettingPreferenceFragment
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity: BaseActivity() {
    override fun onGetLayoutViewId(): Int = R.layout.activity_setting
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar(toolbar, displayShowTitleEnabled = true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingPreferenceFragment())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}