/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.account.model.TmdbLibraryList
import com.norbertotaveras.reels.domain.account.usecase.DisconnectTmdbAccountUseCase
import com.norbertotaveras.reels.domain.account.usecase.FinishTmdbAuthenticationUseCase
import com.norbertotaveras.reels.domain.account.usecase.ObserveTmdbAccountUseCase
import com.norbertotaveras.reels.domain.account.usecase.ObserveTmdbLibraryLastSyncedAtUseCase
import com.norbertotaveras.reels.domain.account.usecase.RefreshTmdbAccountUseCase
import com.norbertotaveras.reels.domain.account.usecase.StartTmdbAuthenticationUseCase
import com.norbertotaveras.reels.domain.account.usecase.UpdateTmdbRatingsSyncUseCase
import com.norbertotaveras.reels.domain.account.usecase.UpdateTmdbWatchlistSyncUseCase
import com.norbertotaveras.reels.domain.library.usecase.ObserveLibraryContentUseCase
import com.norbertotaveras.reels.domain.network.usecase.ObserveNetworkConnectivityUseCase
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.settings.model.AppTheme
import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.domain.settings.model.PlanPreferences
import com.norbertotaveras.reels.domain.settings.usecase.ClearImageCacheUseCase
import com.norbertotaveras.reels.domain.settings.usecase.GetImageCacheSizeUseCase
import com.norbertotaveras.reels.domain.settings.usecase.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.settings.usecase.UpdateImageQualityUseCase
import com.norbertotaveras.reels.domain.settings.usecase.UpdateLanguageRegionUseCase
import com.norbertotaveras.reels.domain.settings.usecase.UpdatePlanPreferencesUseCase
import com.norbertotaveras.reels.domain.settings.usecase.UpdateThemeUseCase
import com.norbertotaveras.reels.presentation.features.settings.dialog.SettingsPreferenceDialog
import com.norbertotaveras.reels.presentation.ui.extensions.toStorageSizeLabel
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel
@Inject
constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeLibraryContentUseCase: ObserveLibraryContentUseCase,
    private val observeTmdbAccountUseCase: ObserveTmdbAccountUseCase,
    private val observeTmdbLibraryLastSyncedAtUseCase: ObserveTmdbLibraryLastSyncedAtUseCase,
    private val observeNetworkConnectivityUseCase: ObserveNetworkConnectivityUseCase,
    private val refreshTmdbAccountUseCase: RefreshTmdbAccountUseCase,
    private val startTmdbAuthenticationUseCase: StartTmdbAuthenticationUseCase,
    private val finishTmdbAuthenticationUseCase: FinishTmdbAuthenticationUseCase,
    private val disconnectTmdbAccountUseCase: DisconnectTmdbAccountUseCase,
    private val updateTmdbWatchlistSyncUseCase: UpdateTmdbWatchlistSyncUseCase,
    private val updateTmdbRatingsSyncUseCase: UpdateTmdbRatingsSyncUseCase,
    private val getImageCacheSizeUseCase: GetImageCacheSizeUseCase,
    private val clearImageCacheUseCase: ClearImageCacheUseCase,
    private val updateImageQualityUseCase: UpdateImageQualityUseCase,
    private val updateLanguageRegionUseCase: UpdateLanguageRegionUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updatePlanPreferencesUseCase: UpdatePlanPreferencesUseCase,
    private val planScheduler: PlanScheduler,
    private val notificationManager: ReelsNotificationManager,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsUiState(
                appVersionLabel = buildAppVersionLabel(),
                appConfigurationLabel = buildAppConfigurationLabel(),
            )
        )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    init {
        observeSettings()
        observeSavedLibrary()
        observeTmdbAccount()
        observeTmdbSyncStatus()
        refreshTmdbAccount()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.ScreenResumed -> {
                loadImageCacheSize()
                refreshPlanAccess()
            }
            is SettingsEvent.PlanPreferenceClicked -> showPreferenceDialog(event.dialog)
            is SettingsEvent.ReleaseReminderSelected ->
                updatePlanPreferences { it.copy(releaseReminder = event.reminder) }
            is SettingsEvent.MovieNightReminderSelected ->
                updatePlanPreferences { it.copy(movieNightReminderMinutes = event.minutes) }
            is SettingsEvent.LiveUpdatesChanged ->
                updatePlanPreferences { it.copy(liveUpdatesEnabled = event.enabled) }
            SettingsEvent.NotificationAccessRequested ->
                openPlanSettings(SettingsEffect.OpenNotificationSettings)
            SettingsEvent.LiveUpdateAccessRequested ->
                openPlanSettings(SettingsEffect.OpenLiveUpdateSettings)
            SettingsEvent.PreciseReminderAccessRequested ->
                openPlanSettings(SettingsEffect.OpenPreciseReminderSettings)
            SettingsEvent.ImageQualityClicked -> onImageQualityClicked()
            is SettingsEvent.ImageQualitySelected -> onImageQualitySelected(event.imageQuality)
            SettingsEvent.LanguageRegionClicked -> onLanguageRegionClicked()
            is SettingsEvent.LanguageRegionSelected ->
                onLanguageRegionSelected(event.languageRegion)
            SettingsEvent.ThemeClicked -> onThemeClicked()
            is SettingsEvent.ThemeSelected -> onThemeSelected(event.theme)
            SettingsEvent.PreferenceDialogDismissed -> onPreferenceDialogDismissed()
            SettingsEvent.SavedLibraryClicked -> onSavedLibraryClicked()
            SettingsEvent.PlansClicked -> onPlansClicked()
            SettingsEvent.ClearImageCacheClicked -> onClearImageCacheClicked()
            SettingsEvent.ClearImageCacheConfirmed -> onClearImageCacheConfirmed()
            SettingsEvent.ClearImageCacheDismissed -> onClearImageCacheDismissed()
            SettingsEvent.TmdbAccountClicked -> onTmdbAccountClicked()
            SettingsEvent.TmdbAccountCallbackReceived -> onTmdbAccountCallbackReceived()
            SettingsEvent.TmdbAccountFinishClicked -> onTmdbAccountFinishClicked()
            SettingsEvent.TmdbAccountDisconnectClicked -> onTmdbAccountDisconnectClicked()
            is SettingsEvent.TmdbWatchlistSyncChanged -> {
                onTmdbWatchlistSyncChanged(enabled = event.enabled)
            }
            is SettingsEvent.TmdbRatingsSyncChanged -> {
                onTmdbRatingsSyncChanged(enabled = event.enabled)
            }
            SettingsEvent.PrivacyClicked -> onPrivacyClicked()
            SettingsEvent.PrivacyNoticeDismissed -> onPrivacyNoticeDismissed()
            SettingsEvent.TmdbAttributionClicked -> onTmdbAttributionClicked()
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase().collect { settings ->
                _state.update { currentState -> currentState.withSettings(settings = settings) }
            }
        }
    }

    private fun refreshPlanAccess() {
        _state.update {
            it.copy(
                canPostNotifications = notificationManager.canPostNotifications(),
                canScheduleExactAlarms = planScheduler.canScheduleExactAlarms(),
                supportsLiveUpdates = notificationManager.supportsLiveUpdates(),
                canPromoteLiveUpdates = notificationManager.canPromoteLiveUpdates(),
            )
        }
    }

    private fun openPlanSettings(effect: SettingsEffect) {
        dismissPreferenceDialog()
        viewModelScope.launch { _effect.emit(effect) }
    }

    private fun updatePlanPreferences(transform: (PlanPreferences) -> PlanPreferences) {
        viewModelScope.launch {
            val previous = _state.value.planPreferences
            runCatching { updatePlanPreferencesUseCase(transform(previous)) }
                .onSuccess { dismissPreferenceDialog() }
                .onFailure {
                    _effect.emit(SettingsEffect.ShowMessage("Unable to update reminder settings."))
                }
        }
    }

    private fun observeSavedLibrary() {
        viewModelScope.launch {
            observeLibraryContentUseCase().collect { content ->
                _state.update { currentState -> currentState.withLibraryContent(content = content) }
            }
        }
    }

    private fun observeTmdbAccount() {
        viewModelScope.launch {
            observeTmdbAccountUseCase().collect { accountState ->
                _state.update { currentState ->
                    currentState.withTmdbAccountState(accountState = accountState)
                }
            }
        }
    }

    private fun observeTmdbSyncStatus() {
        viewModelScope.launch {
            val lastSyncedAt =
                observeSettingsUseCase()
                    .map { settings -> settings.languageRegion.languageCode }
                    .distinctUntilChanged()
                    .flatMapLatest { language ->
                        combine(
                            observeTmdbLibraryLastSyncedAtUseCase(
                                list = TmdbLibraryList.Watchlist,
                                language = language,
                            ),
                            observeTmdbLibraryLastSyncedAtUseCase(
                                list = TmdbLibraryList.Rated,
                                language = language,
                            ),
                        ) { watchlistSyncedAt, ratingsSyncedAt ->
                            listOfNotNull(watchlistSyncedAt, ratingsSyncedAt).maxOrNull()
                        }
                    }

            combine(lastSyncedAt, observeNetworkConnectivityUseCase()) { syncedAtMillis, isOnline ->
                    syncedAtMillis to isOnline
                }
                .collect { (syncedAtMillis, isOnline) ->
                    _state.update { currentState ->
                        currentState.copy(
                            tmdbLastSyncedAtMillis = syncedAtMillis,
                            isOnline = isOnline,
                        )
                    }
                }
        }
    }

    private fun refreshTmdbAccount() {
        viewModelScope.launch { refreshTmdbAccountUseCase() }
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
        _state.update { currentState -> currentState.copy(activePreferenceDialog = dialog) }
    }

    private fun dismissPreferenceDialog() {
        _state.update { currentState -> currentState.copy(activePreferenceDialog = null) }
    }

    private fun onSavedLibraryClicked() {
        viewModelScope.launch { _effect.emit(SettingsEffect.NavigateToLibrary) }
    }

    private fun onPlansClicked() {
        viewModelScope.launch { _effect.emit(SettingsEffect.NavigateToPlans) }
    }

    private fun onClearImageCacheClicked() {
        _state.update { currentState -> currentState.copy(isClearImageCacheDialogVisible = true) }
    }

    private fun onClearImageCacheConfirmed() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isClearImageCacheDialogVisible = false)
            }

            runCatching { clearImageCacheUseCase() }
                .onSuccess { clearedBytes ->
                    updateImageCacheLabel(sizeBytes = 0L)
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message =
                                "Image cache cleared · " +
                                    "${clearedBytes.toStorageSizeLabel()} freed"
                        )
                    )
                }
                .onFailure {
                    _effect.emit(
                        SettingsEffect.ShowMessage(message = "Unable to clear image cache.")
                    )
                }
        }
    }

    private fun onClearImageCacheDismissed() {
        _state.update { currentState -> currentState.copy(isClearImageCacheDialogVisible = false) }
    }

    private fun loadImageCacheSize() {
        viewModelScope.launch {
            runCatching { getImageCacheSizeUseCase() }
                .onSuccess { sizeBytes -> updateImageCacheLabel(sizeBytes = sizeBytes) }
        }
    }

    private fun updateImageCacheLabel(sizeBytes: Long) {
        _state.update { currentState ->
            currentState.copy(
                imageCacheSizeLabel = sizeBytes.toStorageSizeLabel(),
                clearImageCacheLabel =
                    "${sizeBytes.toStorageSizeLabel()} · Frees cached poster images",
            )
        }
    }

    private fun onTmdbAccountClicked() {
        runTmdbAccountAction {
            startTmdbAuthenticationUseCase()
                .onSuccess { approvalUrl ->
                    _effect.emit(SettingsEffect.OpenExternalUrl(url = approvalUrl))
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message = "Approve Reels in TMDB to finish connecting."
                        )
                    )
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message =
                                error.toUserMessage(fallback = "Unable to start TMDB connection.")
                        )
                    )
                }
        }
    }

    private fun onTmdbAccountCallbackReceived() {
        if (_state.value.isTmdbAccountConnected) return
        onTmdbAccountFinishClicked()
    }

    private fun onTmdbAccountFinishClicked() {
        runTmdbAccountAction {
            finishTmdbAuthenticationUseCase()
                .onSuccess {
                    _effect.emit(SettingsEffect.ShowMessage(message = "TMDB account connected."))
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message =
                                error.toUserMessage(
                                    fallback = "Approve access in TMDB before finishing."
                                )
                        )
                    )
                }
        }
    }

    private fun onTmdbAccountDisconnectClicked() {
        runTmdbAccountAction {
            disconnectTmdbAccountUseCase()
                .onSuccess {
                    _effect.emit(SettingsEffect.ShowMessage(message = "TMDB account disconnected."))
                }
                .onFailure { error ->
                    _effect.emit(
                        SettingsEffect.ShowMessage(
                            message =
                                error.toUserMessage(fallback = "Unable to disconnect TMDB account.")
                        )
                    )
                }
        }
    }

    private fun onTmdbWatchlistSyncChanged(enabled: Boolean) {
        viewModelScope.launch { updateTmdbWatchlistSyncUseCase(enabled = enabled) }
    }

    private fun onTmdbRatingsSyncChanged(enabled: Boolean) {
        viewModelScope.launch { updateTmdbRatingsSyncUseCase(enabled = enabled) }
    }

    private fun runTmdbAccountAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            if (_state.value.isTmdbAccountLoading) return@launch

            _state.update { currentState -> currentState.copy(isTmdbAccountLoading = true) }

            try {
                action()
            } finally {
                _state.update { currentState -> currentState.copy(isTmdbAccountLoading = false) }
            }
        }
    }

    private fun onPrivacyClicked() {
        _state.update { currentState -> currentState.copy(isPrivacyNoticeVisible = true) }
    }

    private fun onPrivacyNoticeDismissed() {
        _state.update { currentState -> currentState.copy(isPrivacyNoticeVisible = false) }
    }

    private fun onTmdbAttributionClicked() {
        viewModelScope.launch {
            _effect.emit(SettingsEffect.OpenExternalUrl(url = TMDB_WEBSITE_URL))
        }
    }
}
