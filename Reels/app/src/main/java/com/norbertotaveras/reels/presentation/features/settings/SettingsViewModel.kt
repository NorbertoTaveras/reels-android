package com.norbertotaveras.reels.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.ImageQualityClicked -> Unit
            SettingsEvent.LanguageRegionClicked -> Unit
            SettingsEvent.ThemeClicked -> Unit
            SettingsEvent.SavedMoviesClicked -> Unit
            SettingsEvent.ClearImageCacheClicked -> Unit
            SettingsEvent.PrivacyClicked -> Unit
            SettingsEvent.TmdbAttributionClicked -> onTmdbAttributionClicked()
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
