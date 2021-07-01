package com.maxtyler.blocked.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings
import com.maxtyler.blocked.repository.ColourSettingsRepository
import com.maxtyler.blocked.repository.UISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
    private val colourSettingsRepository: ColourSettingsRepository
) : ViewModel() {
    private val colourSettingsFlow: Flow<List<Pair<Int, ColourSettings>>>
        get() = colourSettingsRepository.getColourSettings().flowOn(Dispatchers.IO)
    private val uiSettingsFlow: Flow<UISettings>
        get() = uiSettingsRepository.getUISettings().flowOn(Dispatchers.IO)

    private val _colourSettings: MutableStateFlow<List<Pair<Int, ColourSettings>>> =
        MutableStateFlow(listOf())
    val colourSettings = _colourSettings.asStateFlow()

    private val _uiSettings: MutableStateFlow<UISettings> = MutableStateFlow(UISettings())
    val uiSettings = _uiSettings.asStateFlow()

    init {
        updateColourSettings()
        updateUiSettings()
    }

    /**
     * Update the viewmodel's current colour settings
     */
    fun updateColourSettings() {
        viewModelScope.launch {
            _colourSettings.value = colourSettingsFlow.first()
        }
    }

    /**
     * Update the viewmodel's current ui settings
     */
    fun updateUiSettings() {
        viewModelScope.launch {
            _uiSettings.value = uiSettingsFlow.first()
        }
    }

    /**
     * Submit the viewmodel
     */
    fun writeUiSettings(uiSettings: UISettings) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uiSettingsRepository.updateSettings(uiSettings)
            }
            _uiSettings.value = uiSettingsFlow.first()
        }
    }

    /**
     * Submit the new colour settings
     */
    fun writeColourSettings(colourSettings: Pair<Int, ColourSettings>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                colourSettingsRepository.updateColourSettings(colourSettings)
            }
            _colourSettings.value = colourSettingsFlow.first()
        }
    }

    fun newColourSettings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                colourSettingsRepository.createNew()
            }
            _colourSettings.value = colourSettingsFlow.first()
        }
    }

    fun deleteColourSettings(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                colourSettingsRepository.deleteColourSettings(id)
            }
            _colourSettings.value = colourSettingsFlow.first()
        }
    }
}