package com.norbertotaveras.reels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsNavHost
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var observeSettingsUseCase: ObserveSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by observeSettingsUseCase().collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (settings.theme) {
                AppTheme.System -> systemInDarkTheme
                AppTheme.Dark -> true
                AppTheme.Light -> false
            }

            ReelsTheme(
                darkTheme = darkTheme,
                imageQuality = settings.imageQuality
            ) {
                ReelsNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
