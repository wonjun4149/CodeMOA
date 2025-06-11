package com.wonjun.codemoa.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.wonjun.codemoa.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    fun getAppSettings(): Flow<AppSettings>
    suspend fun updateThemeMode(themeMode: Int)
    suspend fun updateLanguage(languageCode: String)
    suspend fun updateNotificationSettings(enabled: Boolean)
    suspend fun updateExpiryNotificationSettings(enabled: Boolean, days: Int)
    suspend fun updateSecuritySettings(appLockEnabled: Boolean, biometricEnabled: Boolean)
    suspend fun getAppSettingsSnapshot(): AppSettings
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _appSettings = MutableStateFlow(loadSettings())

    override fun getAppSettings(): Flow<AppSettings> = _appSettings.asStateFlow()

    override suspend fun updateThemeMode(themeMode: Int) {
        sharedPreferences.edit()
            .putInt("theme_mode", themeMode)
            .apply()
        _appSettings.value = _appSettings.value.copy(themeMode = themeMode)
    }

    override suspend fun updateLanguage(languageCode: String) {
        sharedPreferences.edit()
            .putString("language_code", languageCode)
            .apply()
        _appSettings.value = _appSettings.value.copy(languageCode = languageCode)
    }

    override suspend fun updateNotificationSettings(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("notification_enabled", enabled)
            .apply()
        _appSettings.value = _appSettings.value.copy(notificationEnabled = enabled)
    }

    override suspend fun updateExpiryNotificationSettings(enabled: Boolean, days: Int) {
        sharedPreferences.edit()
            .putBoolean("expiry_notification_enabled", enabled)
            .putInt("expiry_notification_days", days)
            .apply()
        _appSettings.value = _appSettings.value.copy(
            expiryNotificationEnabled = enabled,
            expiryNotificationDays = days
        )
    }

    override suspend fun updateSecuritySettings(appLockEnabled: Boolean, biometricEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("app_lock_enabled", appLockEnabled)
            .putBoolean("biometric_enabled", biometricEnabled)
            .apply()
        _appSettings.value = _appSettings.value.copy(
            appLockEnabled = appLockEnabled,
            biometricEnabled = biometricEnabled
        )
    }

    override suspend fun getAppSettingsSnapshot(): AppSettings {
        return _appSettings.value
    }

    private fun loadSettings(): AppSettings {
        return AppSettings(
            themeMode = sharedPreferences.getInt("theme_mode", 0),
            languageCode = sharedPreferences.getString("language_code", "system") ?: "system",
            notificationEnabled = sharedPreferences.getBoolean("notification_enabled", true),
            expiryNotificationEnabled = sharedPreferences.getBoolean("expiry_notification_enabled", true),
            expiryNotificationDays = sharedPreferences.getInt("expiry_notification_days", 7),
            appLockEnabled = sharedPreferences.getBoolean("app_lock_enabled", false),
            biometricEnabled = sharedPreferences.getBoolean("biometric_enabled", false)
        )
    }
}