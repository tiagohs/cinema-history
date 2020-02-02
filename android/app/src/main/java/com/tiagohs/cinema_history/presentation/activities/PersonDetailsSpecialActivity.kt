package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.BaseActivity

class PersonDetailsSpecialActivity: BaseActivity() {
    override fun onGetLayoutViewId(): Int = R.layout.fragment_person_details_special
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, PersonDetailsSpecialActivity::class.java)

            return intent
        }
    }
}