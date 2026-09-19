package com.norbertotaveras.reels.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateImageQualityUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateLanguageRegionUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateImageQualityUseCase: UpdateImageQualityUseCase,
    private val updateLanguageRegionUseCase: UpdateLanguageRegionUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsUiState(appVersionLabel = BuildConfig.VERSION_NAME)
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    init {
        observeSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.ImageQualityClicked -> onImageQualityClicked()
            is SettingsEvent.ImageQualitySelected -> onImageQualitySelected(event.imageQuality)
            SettingsEvent.LanguageRegionClicked -> onLanguageRegionClicked()
            is SettingsEvent.LanguageRegionSelected -> onLanguageRegionSelected(event.languageRegion)
            SettingsEvent.ThemeClicked -> onThemeClicked()
            is SettingsEvent.ThemeSelected -> onThemeSelected(event.theme)
            SettingsEvent.PreferenceDialogDismissed -> onPreferenceDialogDismissed()
            SettingsEvent.SavedMoviesClicked -> onSavedMoviesClicked()
            SettingsEvent.ClearImageCacheClicked -> onClearImageCacheClicked()
            SettingsEvent.PrivacyClicked -> onPrivacyClicked()
            SettingsEvent.PrivacyNoticeDismissed -> onPrivacyNoticeDismissed()
            SettingsEvent.TmdbAttributionClicked -> onTmdbAttributionClicked()
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase().collect { settings ->
                _state.update { currentState ->
                    currentState.copy(
                        imageQualityLabel = settings.imageQuality.label,
                        languageRegionLabel = settings.languageRegion.label,
                        themeLabel = settings.theme.label,
                        selectedImageQuality = settings.imageQuality,
                        selectedLanguageRegion = settings.languageRegion,
                        selectedTheme = settings.theme
                    )
                }
            }
        }
    }

    private fun onImageQualityClicked() {
        showPreferenceDialog(SettingsPreferenceDialog.ImageQuality)
    }

    private fun onImageQualitySelected(imageQuality: ImageQuality) {
        viewModelScope.launch {
            dismissPreferenceDialog()
            updateImageQualityUseCase(imageQuality)
        }
    }

    private fun onLanguageRegionClicked() {
        showPreferenceDialog(SettingsPreferenceDialog.LanguageRegion)
    }

    private fun onLanguageRegionSelected(languageRegion: LanguageRegion) {
        viewModelScope.launch {
            dismissPreferenceDialog()
            updateLanguageRegionUseCase(languageRegion)
        }
    }

    private fun onThemeClicked() {
        showPreferenceDialog(SettingsPreferenceDialog.Theme)
    }

    private fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            dismissPreferenceDialog()
            updateThemeUseCase(theme)
        }
    }

    private fun onPreferenceDialogDismissed() {
        dismissPreferenceDialog()
    }

    private fun showPreferenceDialog(dialog: SettingsPreferenceDialog) {
        _state.update { currentState ->
            currentState.copy(activePreferenceDialog = dialog)
        }
    }

    private fun dismissPreferenceDialog() {
        _state.update { currentState ->
            currentState.copy(activePreferenceDialog = null)
        }
    }

    private fun onSavedMoviesClicked() {
        viewModelScope.launch {
            _effect.emit(SettingsEffect.NavigateToFavorites)
        }
    }

    private fun onClearImageCacheClicked() {
        viewModelScope.launch {
            _effect.emit(SettingsEffect.ClearImageCache)
        }
    }

    private fun onPrivacyClicked() {
        _state.update { currentState ->
            currentState.copy(isPrivacyNoticeVisible = true)
        }
    }

    private fun onPrivacyNoticeDismissed() {
        _state.update { currentState ->
            currentState.copy(isPrivacyNoticeVisible = false)
        }
    }

    private fun onTmdbAttributionClicked() {
        viewModelScope.launch {
            _effect.emit(
                SettingsEffect.OpenExternalUrl(url = TMDB_WEBSITE_URL)
            )
        }
    }

    private companion object {
        const val TMDB_WEBSITE_URL = "https://www.themoviedb.org/"
    }
}
