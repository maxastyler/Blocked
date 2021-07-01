package com.maxtyler.blocked.ui

import androidx.lifecycle.ViewModel
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings
import com.maxtyler.blocked.repository.ColourSettingsRepository
import com.maxtyler.blocked.repository.UISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
    private val colourSettingsRepository: ColourSettingsRepository
) : ViewModel() {
    val colourSettings: Flow<List<Pair<Int, ColourSettings>>>
        get() = colourSettingsRepository.getColourSettings().flowOn(Dispatchers.IO)
    val uiSettings: Flow<UISettings>
        get() = uiSettingsRepository.getUISettings().flowOn(Dispatchers.IO)
}