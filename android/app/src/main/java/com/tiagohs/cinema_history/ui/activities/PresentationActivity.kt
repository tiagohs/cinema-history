package com.tiagohs.cinema_history.ui.activities

import android.os.Bundle
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import kotlinx.android.synthetic.main.activity_presentation.*

class PresentationActivity: BaseActivity() {
    override fun onGetLayoutViewId(): Int = R.layout.activity_presentation

    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startButton.setTextColor(resources.getColor(android.R.color.white))
    }
}