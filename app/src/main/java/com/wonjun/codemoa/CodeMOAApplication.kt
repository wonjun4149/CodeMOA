package com.wonjun.codemoa

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CodeMOAApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 저장된 언어 설정 적용
        applyLanguageSettings()

        // 저장된 테마 설정 적용
        applyThemeSettings()
    }

    private fun applyLanguageSettings() {
        val savedLanguage = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("language_code", "system") ?: "system"

        when (savedLanguage) {
            "system" -> {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
            else -> {
                val localeList = LocaleListCompat.forLanguageTags(savedLanguage)
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }
    }

    private fun applyThemeSettings() {
        val savedTheme = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getInt("theme_mode", 0)

        val nightMode = when (savedTheme) {
            1 -> AppCompatDelegate.MODE_NIGHT_NO    // Light
            2 -> AppCompatDelegate.MODE_NIGHT_YES   // Dark
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}