package com.tiagohs.cinema_history.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.onboarding.Onboarding
import com.tiagohs.cinema_history.onboarding.OnboardingActivity

class SplashScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Primeira abertura: onboarding antes da Home (a Home pede o consentimento de anúncios depois dele).
        startActivity(
            if (Onboarding.isNeeded(this)) OnboardingActivity.newIntent(this) else HomeActivity.newIntent(this)
        )
        finish()
    }

}
