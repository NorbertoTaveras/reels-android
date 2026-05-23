package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class UpdateImageQualityUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(imageQuality: ImageQuality) {
        repository.updateImageQuality(imageQuality = imageQuality)
    }
}
