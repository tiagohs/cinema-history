package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivitySettingBinding
import android.content.Intent
import android.os.Bundle
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.fragments.SettingPreferenceFragment
import com.tiagohs.helpers.extensions.startFragment

class SettingActivity: BaseActivity<ActivitySettingBinding>() {
    override fun inflateBinding(inflater: LayoutInflater) = ActivitySettingBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar(binding.toolbar, displayShowTitleEnabled = true)

        startFragment(R.id.container, SettingPreferenceFragment())
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}