package com.tiagohs.cinema_history.onboarding

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.tiagohs.cinema_history.BuildConfig

/**
 * Controle do onboarding da primeira abertura (versão guardada em SharedPreferences).
 *
 * Quem já usava o app antes desta versão (atualização, não instalação nova) não vê o onboarding.
 * Para mostrar um onboarding novo a todos no futuro, aumente [VERSION] e ajuste [isNeeded].
 */
object Onboarding {

    const val VERSION = 1

    private const val PREFS = "onboarding"
    private const val KEY_VERSION = "version"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isNeeded(context: Context): Boolean {
        if (prefs(context).getInt(KEY_VERSION, 0) >= VERSION) return false

        if (isUpdateFromOlderVersion(context)) {
            markDone(context)
            return false
        }
        return true
    }

    fun markDone(context: Context) {
        prefs(context).edit().putInt(KEY_VERSION, VERSION).apply()
    }

    /** Só em debug (opção escondida em Sobre). */
    fun reset(context: Context) {
        if (!BuildConfig.DEBUG) return
        prefs(context).edit().remove(KEY_VERSION).putBoolean(KEY_FORCE, true).apply()
    }

    private const val KEY_FORCE = "debug_force"

    /** Instalação antiga atualizada: a data de instalação é diferente da data da última atualização. */
    private fun isUpdateFromOlderVersion(context: Context): Boolean {
        if (BuildConfig.DEBUG && prefs(context).getBoolean(KEY_FORCE, false)) return false
        return runCatching {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            info.firstInstallTime != info.lastUpdateTime
        }.getOrDefault(false)
    }
}
