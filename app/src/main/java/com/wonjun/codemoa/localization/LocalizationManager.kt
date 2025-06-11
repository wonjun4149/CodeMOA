package com.wonjun.codemoa.localization

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

enum class SupportedLanguage(val code: String, val displayName: String, val nativeName: String) {
    KOREAN("ko", "Korean", "한국어"),
    ENGLISH("en", "English", "English"),
    JAPANESE("ja", "Japanese", "日本語"),
    SYSTEM("system", "System Default", "시스템 기본값")
}

class LocalizationManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "language_code"
    }

    fun setLanguage(language: SupportedLanguage) {
        // SharedPreferences에 저장
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE, language.code)
            .apply()

        // 언어 적용
        applyLanguage(language)
    }

    fun getCurrentLanguage(): SupportedLanguage {
        val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, SupportedLanguage.SYSTEM.code)
        return SupportedLanguage.values().find { it.code == savedLanguage }
            ?: SupportedLanguage.SYSTEM
    }

    fun getAvailableLanguages(): List<SupportedLanguage> {
        return SupportedLanguage.values().toList()
    }

    private fun applyLanguage(language: SupportedLanguage) {
        when (language) {
            SupportedLanguage.SYSTEM -> {
                // 시스템 기본값 사용
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
            else -> {
                // 특정 언어 설정
                val localeList = LocaleListCompat.forLanguageTags(language.code)
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }
    }

    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }

    fun getCurrentLanguageDisplayName(context: Context): String {
        val currentLanguage = getCurrentLanguage()
        return when (currentLanguage) {
            SupportedLanguage.KOREAN -> context.getString(com.wonjun.codemoa.R.string.language_korean)
            SupportedLanguage.ENGLISH -> context.getString(com.wonjun.codemoa.R.string.language_english)
            SupportedLanguage.JAPANESE -> context.getString(com.wonjun.codemoa.R.string.language_japanese)
            SupportedLanguage.SYSTEM -> context.getString(com.wonjun.codemoa.R.string.language_system_default)
        }
    }
}