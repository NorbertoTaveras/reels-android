/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.usecase.account.DisconnectTmdbAccountUseCase
import com.norbertotaveras.reels.domain.usecase.account.FinishTmdbAuthenticationUseCase
import com.norbertotaveras.reels.domain.usecase.account.ObserveTmdbAccountUseCase
import com.norbertotaveras.reels.domain.usecase.account.StartTmdbAuthenticationUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveFavoriteMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveSavedPeopleUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateImageQualityUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateLanguageRegionUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateThemeUseCase
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase,
    private val observeSavedPeopleUseCase: ObserveSavedPeopleUseCase,
    private val observeTmdbAccountUseCase: ObserveTmdbAccountUseCase,
    private val startTmdbAuthenticationUseCase: StartTmdbAuthenticationUseCase,
    private val finishTmdbAuthenticationUseCase: FinishTmdbAuthenticationUseCase,
    private val disconnectTmdbAccountUseCase: DisconnectTmdbAccountUseCase,
    private val updateImageQualityUseCase: UpdateImageQualityUseCase,
    private val updateLanguageRegionUseCase: UpdateLanguageRegionUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsUiState(
            appVersionLabel = buildAppVersionLabel(),
            appConfigurationLabel = buildAppConfigurationLabel()
        )
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    init {
        observeSettings()
        observeSavedLibrary()
        observeTmdbAccount()
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
            SettingsEvent.SavedLibraryClicked -> onSavedLibraryClicked()
            SettingsEvent.ClearImageCacheClicked -> onClearImageCacheClicked()
            SettingsEvent.ClearImageCacheConfirmed -> onClearImageCacheConfirmed()
            SettingsEvent.ClearImageCacheDismissed -> onClearImageCacheDismissed()
            SettingsEvent.TmdbAccountClicked -> onTmdbAccountClicked()
            SettingsEvent.TmdbAccountFinishClicked -> onTmdbAccountFinishClicked()
            SettingsEvent.TmdbAccountDisconnectClicked -> onTmdbAccountDisconnectClicked()
            SettingsEvent.UiStatesReferenceClicked -> onUiStatesReferenceClicked()
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

    private fun observeSavedLibrary() {
        viewModelScope.launch {
            combine(
                observeFavoriteMoviesUseCase(),
                observeSavedPeopleUseCase()
            ) { movies, people ->
                buildSavedLibraryLabel(
                    movieCount = movies.size,
                    personCount = people.size
                )
            }.collect { savedLibraryLabel ->
                _state.update { currentState ->
                    currentState.copy(savedLibraryLabel = savedLibraryLabel)
                }
            }
        }
    }

    private fun observeTmdbAccount() {
        viewModelScope.launch {
            observeTmdbAccountUseCase().collect { accountState ->
                _state.update { currentState ->
                    val account = accountState.account
                    val accountName = account?.displayName
                        ?.takeIf { displayName -> displayName.isNotBlank() }
                        ?: account?.username
                        ?: ""
                    currentState.copy(
                        isTmdbAccountConnected = accountState.isConnected,
                        isTmdbAccountApprovalPending = accountState.isApprovalPending,
                        tmdbAccountName = accountName,
                        tmdbAccountTitle = when {
                            accountState.isConnected -> "TMDB Account"
                            accountState.isApprovalPending -> "TMDB Approval Pending"
                            else -> "Connect TMDB Account"
                        },
                        tmdbAccountLabel = when {
                            accountState.isConnected -> {
                                "Connected as ${accountName.ifBlank { "TMDB user" }}"
                            }
                            accountState.isApprovalPending -> {
                                "Approve access in your browser, then finish connection"
                            }
                            else -> "Sync watchlist and ratings with TMDB"
                        }
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
            _effect.emit(SettingsEffect.ShowMessage(message = "Image quality updated"))
        }
    }

    private fun onLanguageRegionClicked() {
        showPreferenceDialog(SettingsPreferenceDialog.LanguageRegion)
    }

    private fun onLanguageRegionSelected(languageRegion: LanguageRegion) {
        viewModelScope.launch {
            dismissPreferenceDialog()
            updateLanguageRegionUseCase(languageRegion)
            _effect.emit(SettingsEffect.ShowMessage(message = "Language and region updated"))
        }
    }

    private fun onThemeClicked() {
        showPreferenceDialog(SettingsPreferenceDialog.Theme)
    }

    private fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            dismissPreferenceDialog()
            updateThemeUseCase(theme)
            _effect.emit(SettingsEffect.ShowMessage(message = "Theme updated"))
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

    private fun onSavedLibraryClicked() {
        viewModelScope.launch {
            _effect.emit(SettingsEffect.NavigateToLibrary)
        }
    }

    private fun onClearImageCacheClicked() {
        _state.update { currentState ->
            currentState.copy(isClearImageCacheDialogVisible = true)
        }
    }

    private fun onClearImageCacheConfirmed() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isClearImageCacheDialogVisible = false)
            }
            _effect.emit(SettingsEffect.ClearImageCache)
        }
    }

    private fun onClearImageCacheDismissed() {
        _state.update { currentState ->
            currentState.copy(isClearImageCacheDialogVisible = false)
        }
    }

    private fun onTmdbAccountClicked() {
        viewModelScope.launch {
            if (_state.value.isTmdbAccountLoading) return@launch

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = true)
            }

            startTmdbAuthenticationUseCase()
                .onSuccess { approvalUrl ->
                    _effect.emit(SettingsEffect.OpenExternalUrl(url = approvalUrl))
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message = "Approve Reels in TMDB, then return to finish."
                        )
                    )
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to start TMDB connection."
                            )
                        )
                    )
                }

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = false)
            }
        }
    }

    private fun onTmdbAccountFinishClicked() {
        viewModelScope.launch {
            if (_state.value.isTmdbAccountLoading) return@launch

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = true)
            }

            finishTmdbAuthenticationUseCase()
                .onSuccess {
                    _effect.emit(SettingsEffect.ShowMessage(message = "TMDB account connected."))
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Approve access in TMDB before finishing."
                            )
                        )
                    )
                }

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = false)
            }
        }
    }

    private fun onTmdbAccountDisconnectClicked() {
        viewModelScope.launch {
            if (_state.value.isTmdbAccountLoading) return@launch

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = true)
            }

            disconnectTmdbAccountUseCase()
                .onSuccess {
                    _effect.emit(SettingsEffect.ShowMessage(message = "TMDB account disconnected."))
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to disconnect TMDB account."
                            )
                        )
                    )
                }

            _state.update { currentState ->
                currentState.copy(isTmdbAccountLoading = false)
            }
        }
    }

    private fun onPrivacyClicked() {
        _state.update { currentState ->
            currentState.copy(isPrivacyNoticeVisible = true)
        }
    }

    private fun onUiStatesReferenceClicked() {
        viewModelScope.launch {
            _effect.emit(SettingsEffect.NavigateToUiStates)
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

        fun buildAppVersionLabel(): String {
            return "${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})"
        }

        fun buildAppConfigurationLabel(): String {
            val flavorLabel = BuildConfig.FLAVOR.toDisplayLabel()
            val buildTypeLabel = BuildConfig.BUILD_TYPE.toDisplayLabel()
            val tokenStatusLabel = if (BuildConfig.TMDB_READ_TOKEN.isBlank()) {
                "Token missing"
            } else {
                "Token configured"
            }

            return "$flavorLabel $buildTypeLabel · $tokenStatusLabel"
        }

        fun buildSavedLibraryLabel(
            movieCount: Int,
            personCount: Int
        ): String {
            if (movieCount == 0 && personCount == 0) {
                return "Manage movies and people"
            }

            val movieLabel = "$movieCount ${if (movieCount == 1) "movie" else "movies"}"
            val personLabel = "$personCount ${if (personCount == 1) "person" else "people"}"
            return "$movieLabel · $personLabel"
        }

        fun String.toDisplayLabel(): String {
            return replaceFirstChar { character ->
                if (character.isLowerCase()) {
                    character.titlecase()
                } else {
                    character.toString()
                }
            }
        }
    }
}
