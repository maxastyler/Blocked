package com.maxtyler.blocked.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings
import com.maxtyler.blocked.repository.ColourSettingsRepository
import com.maxtyler.blocked.repository.UISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
    private val colourSettingsRepository: ColourSettingsRepository
) : ViewModel() {
    val colourSettingsFlow: Flow<List<Pair<Int, ColourSettings>>>
        get() = colourSettingsRepository.getColourSettings().flowOn(Dispatchers.IO)
    val uiSettingsFlow: Flow<UISettings>
        get() = uiSettingsRepository.getUISettings().flowOn(Dispatchers.IO)


    /**
     * Submit the new uiSettings
     * @param uiSettings The new settings to add to the database
     */
    fun writeUiSettings(uiSettings: UISettings) {
        viewModelScope.launch(Dispatchers.IO) {
            uiSettingsRepository.updateSettings(uiSettings)

        }
    }

    /**
     * Submit the new colour settings
     */
    fun writeColourSettings(colourSettings: Pair<Int, ColourSettings>) {
        viewModelScope.launch(Dispatchers.IO) {
            colourSettingsRepository.updateColourSettings(colourSettings)
        }
    }

    fun newColourSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            colourSettingsRepository.createNew()
        }
    }

    fun deleteColourSettings(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            colourSettingsRepository.deleteColourSettings(id)
        }
    }
}