package com.wonjun.codemoa.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wonjun.codemoa.R
import com.wonjun.codemoa.localization.LocalizationManager
import com.wonjun.codemoa.localization.SupportedLanguage
import com.wonjun.codemoa.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val localizationManager = remember { LocalizationManager(context) }
    val uiState by viewModel.uiState.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (!uiState.isLoading) {
            // 언어 설정
            SettingsSection(title = stringResource(R.string.settings_language)) {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.settings_language),
                    subtitle = localizationManager.getCurrentLanguageDisplayName(context),
                    onClick = { showLanguageDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 테마 설정
            SettingsSection(title = stringResource(R.string.settings_theme)) {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = stringResource(R.string.settings_theme),
                    subtitle = getThemeDisplayName(uiState.appSettings.themeMode),
                    onClick = { showThemeDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 알림 설정
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.notification_enabled),
                        subtitle = if (uiState.appSettings.notificationEnabled) "활성화됨" else "비활성화됨",
                        onClick = { showNotificationDialog = true }
                    )

                    if (uiState.appSettings.notificationEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // 만료일 알림 토글
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.expiry_notification),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = stringResource(R.string.days_before, uiState.appSettings.expiryNotificationDays),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = uiState.appSettings.expiryNotificationEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.updateExpiryNotificationSettings(
                                        enabled,
                                        uiState.appSettings.expiryNotificationDays
                                    )
                                }
                            )
                        }

                        // 알림 일수 슬라이더
                        if (uiState.appSettings.expiryNotificationEnabled) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.notification_days),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Slider(
                                    value = uiState.appSettings.expiryNotificationDays.toFloat(),
                                    onValueChange = { value ->
                                        viewModel.updateExpiryNotificationSettings(
                                            uiState.appSettings.expiryNotificationEnabled,
                                            value.toInt()
                                        )
                                    },
                                    valueRange = 1f..30f,
                                    steps = 29
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 백업 및 복원
            SettingsSection(title = stringResource(R.string.settings_backup)) {
                SettingsItem(
                    icon = Icons.Default.QrCode,
                    title = stringResource(R.string.settings_backup),
                    subtitle = "QR코드 백업 및 복원",
                    onClick = {
                        // TODO: 백업 화면으로 이동
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 보안 설정
            SettingsSection(title = stringResource(R.string.settings_security)) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.security_app_lock),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "앱 실행 시 인증 요구",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.appSettings.appLockEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.updateSecuritySettings(
                                    enabled,
                                    uiState.appSettings.biometricEnabled
                                )
                            }
                        )
                    }

                    if (uiState.appSettings.appLockEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.security_biometric),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "지문/Face ID 인증",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = uiState.appSettings.biometricEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.updateSecuritySettings(
                                        uiState.appSettings.appLockEnabled,
                                        enabled
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 앱 정보
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.settings_about),
                    subtitle = "버전 1.0.0",
                    onClick = {
                        // TODO: 앱 정보 화면으로 이동
                    }
                )
            }
        }
    }

    // 언어 선택 다이얼로그
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            localizationManager = localizationManager,
            onLanguageSelected = { language ->
                viewModel.updateLanguage(language.code)
                localizationManager.setLanguage(language)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // 테마 선택 다이얼로그
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.appSettings.themeMode,
            onThemeSelected = { themeMode ->
                viewModel.updateTheme(themeMode)
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // 알림 설정 다이얼로그
    if (showNotificationDialog) {
        NotificationSettingsDialog(
            currentEnabled = uiState.appSettings.notificationEnabled,
            onSettingChanged = { enabled ->
                viewModel.updateNotificationSettings(enabled)
            },
            onDismiss = { showNotificationDialog = false }
        )
    }
}

@Composable
private fun getThemeDisplayName(themeMode: Int): String {
    return when (themeMode) {
        1 -> stringResource(R.string.theme_light)
        2 -> stringResource(R.string.theme_dark)
        else -> stringResource(R.string.theme_system)
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    localizationManager: LocalizationManager,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(localizationManager.getCurrentLanguage()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                localizationManager.getAvailableLanguages().forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = when (language) {
                                    SupportedLanguage.KOREAN -> stringResource(R.string.language_korean)
                                    SupportedLanguage.ENGLISH -> stringResource(R.string.language_english)
                                    SupportedLanguage.JAPANESE -> stringResource(R.string.language_japanese)
                                    SupportedLanguage.SYSTEM -> stringResource(R.string.language_system_default)
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = language.nativeName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onLanguageSelected(selectedLanguage)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: Int,
    onThemeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    val themes = listOf(
        0 to stringResource(R.string.theme_system),
        1 to stringResource(R.string.theme_light),
        2 to stringResource(R.string.theme_dark)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column {
                themes.forEach { (themeMode, themeName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == themeMode,
                            onClick = { selectedTheme = themeMode }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = themeName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onThemeSelected(selectedTheme)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun NotificationSettingsDialog(
    currentEnabled: Boolean,
    onSettingChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var enabled by remember { mutableStateOf(currentEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_notifications)) },
        text = {
            Column {
                Text(
                    text = "푸시 알림을 통해 기프티콘 만료일을 알려드립니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.notification_enabled),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSettingChanged(enabled)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}