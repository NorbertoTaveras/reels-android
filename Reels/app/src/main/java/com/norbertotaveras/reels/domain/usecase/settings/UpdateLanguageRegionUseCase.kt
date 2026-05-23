package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class UpdateLanguageRegionUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(languageRegion: LanguageRegion) {
        repository.updateLanguageRegion(languageRegion = languageRegion)
    }
}
