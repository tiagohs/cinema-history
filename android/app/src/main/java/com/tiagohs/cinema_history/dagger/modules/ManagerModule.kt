package com.tiagohs.cinema_history.dagger.modules

import android.content.Context
import com.tiagohs.domain.managers.AudioManager
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.helpers.Constants
import dagger.Module
import dagger.Provides

@Module
class ManagerModule {

    @Provides
    fun provideAudioManager(): AudioManager = AudioManager()

    @Provides
    fun provideSettingsManager(context: Context): SettingsManager {
        val sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES.PREF_SETTINGS_NAME, Constants.SHARED_PREFERENCES.PRIVATE_MODE)

        return SettingsManager(context, sharedPreferences)
    }
}