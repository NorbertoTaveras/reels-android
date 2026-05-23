package com.norbertotaveras.reels.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.settings.AppSettings
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
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    private var currentSettings = AppSettings()

    init {
        observeSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.ImageQualityClicked -> onImageQualityClicked()
            SettingsEvent.LanguageRegionClicked -> onLanguageRegionClicked()
            SettingsEvent.ThemeClicked -> onThemeClicked()
            SettingsEvent.SavedMoviesClicked -> Unit
            SettingsEvent.ClearImageCacheClicked -> Unit
            SettingsEvent.PrivacyClicked -> Unit
            SettingsEvent.TmdbAttributionClicked -> onTmdbAttributionClicked()
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase().collect { settings ->
                currentSettings = settings
                _state.update { currentState ->
                    currentState.copy(
                        imageQualityLabel = settings.imageQuality.label,
                        languageRegionLabel = settings.languageRegion.label,
                        themeLabel = settings.theme.label
                    )
                }
            }
        }
    }

    private fun onImageQualityClicked() {
        viewModelScope.launch {
            updateImageQualityUseCase(currentSettings.imageQuality.next())
        }
    }

    private fun onLanguageRegionClicked() {
        viewModelScope.launch {
            updateLanguageRegionUseCase(currentSettings.languageRegion.next())
        }
    }

    private fun onThemeClicked() {
        viewModelScope.launch {
            updateThemeUseCase(currentSettings.theme.next())
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
