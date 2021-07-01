package com.maxtyler.blocked.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.SettingType
import com.maxtyler.blocked.game.UISettings

typealias TempUiSettings = Pair<Map<String, Pair<SettingType<out Number>, String>>, Pair<Int?, ColourSettings>>

@Composable
fun SettingsView(viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiSettings by viewModel.uiSettingsFlow.collectAsState(UISettings())
    var tempUiSettings by remember(uiSettings) {
        mutableStateOf(
            Pair(
                uiSettings.settings.map { (k, v) -> k to Pair(v, v.value.toString()) }.toMap(),
                uiSettings.colourSettings
            )
        )
    }

    val colourSettings by viewModel.colourSettingsFlow.collectAsState(listOf())

    val scrollState = rememberLazyListState()

    Box() {
        Column() {
            UISettingsView(
                tempUiSettings = tempUiSettings,
                { tempUiSettings = tempUiSettings.copy() },
                { viewModel.writeUiSettings({it}) })
            Button(onClick = { viewModel.newColourSettings() }) {
                Text("New colour settings")
            }
            LazyColumn(state = scrollState) {
                itemsIndexed(colourSettings) { i, (id, settings) ->
                    ColourSettingsView(
                        colourSettings = settings,
                        { viewModel.writeColourSettings(Pair(id, it)) },
                        { viewModel.deleteColourSettings(id) })
                }
            }
        }
    }
}

@Composable
fun UISettingsView(
    tempUiSettings: TempUiSettings,
    onUpdate: (TempUiSettings) -> Unit = {},
    onSubmit: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(Modifier.scrollable(scrollState, orientation = Orientation.Vertical)) {
        tempUiSettings.first.forEach { (k, v) ->
            Row() {
                Text(
                    v.first.name,
                    softWrap = true,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SubmittableNumInput(value = v.second, onUpdate = { s -> })
            }
        }
        Button(onClick = onSubmit) {
            Text("Save values")
        }
    }
}

@Composable
fun SubmittableNumInput(
    value: String,
    modifier: Modifier = Modifier,
    onUpdate: (String) -> Unit = {},
    onRevert: () -> Unit = {},
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TextField(
            value = value,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = { onUpdate(it) }
        )
        Button(onClick = onRevert) {
            Text("R")
        }
    }
}

@Composable
fun ColourSettingsView(
    colourSettings: ColourSettings,
    onUpdate: (ColourSettings) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        ColourPicker(
            colourSettings.backgroundColour,
            { onUpdate(colourSettings.copy(backgroundColour = it)) })
        ColourPicker(
            color = colourSettings.shadowColour,
            { onUpdate(colourSettings.copy(shadowColour = it)) })
        ColourPicker(
            color = colourSettings.LColour,
            { onUpdate(colourSettings.copy(LColour = it)) })
        ColourPicker(
            color = colourSettings.ZColour,
            { onUpdate(colourSettings.copy(ZColour = it)) })
        ColourPicker(
            color = colourSettings.SColour,
            { onUpdate(colourSettings.copy(SColour = it)) })
        ColourPicker(
            color = colourSettings.TColour,
            { onUpdate(colourSettings.copy(TColour = it)) })
        ColourPicker(
            color = colourSettings.OColour,
            { onUpdate(colourSettings.copy(OColour = it)) })
        ColourPicker(
            color = colourSettings.JColour,
            { onUpdate(colourSettings.copy(JColour = it)) })
        ColourPicker(
            color = colourSettings.IColour,
            { onUpdate(colourSettings.copy(IColour = it)) })
        Button(onClick = onDelete) {
            Text("X")
        }
    }
}

@Composable
fun ColourPicker(color: Color, onUpdate: (Color) -> Unit = {}) {
    Box(
        modifier = Modifier
            .background(color = color)
            .size(20.dp)
    )
    {
        Text("Hi")
    }
}