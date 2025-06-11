@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: com.wonjun.codemoa.data.repository.SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getAppSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    appSettings = settings,
                    isLoading = false
                )
            }
        }
    }

    fun updateTheme(themeMode: Int) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(themeMode)
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(languageCode)
        }
    }

    fun updateNotificationSettings(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationSettings(enabled)
        }
    }

    fun updateExpiryNotificationSettings(enabled: Boolean, days: Int) {
        viewModelScope.launch {
            settingsRepository.updateExpiryNotificationSettings(enabled, days)
        }
    }

    fun updateSecuritySettings(appLockEnabled: Boolean, biometricEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSecuritySettings(appLockEnabled, biometricEnabled)
        }
    }
}

data class SettingsUiState(
    val appSettings: com.wonjun.codemoa.data.model.AppSettings = com.wonjun.codemoa.data.model.AppSettings(),
    val isLoading: Boolean = true,
    val error: String? = null
)