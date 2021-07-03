package com.maxtyler.blocked.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings

@Composable
fun SettingsView(viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiSettings by viewModel.uiSettingsFlow.collectAsState(UISettings())

    val colourSettings by viewModel.colourSettingsFlow.collectAsState(listOf())

    val scrollState = rememberLazyListState()

    Box() {
        Column() {
            UISettingsView(
                uiSettings = uiSettings,
                { viewModel.writeUiSettings(it) })
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
    uiSettings: UISettings,
    onSubmit: (UISettings) -> Unit = {},
) {
    var tempUiSettings by remember(uiSettings) {
        mutableStateOf(
            TempUISettings(uiSettings)
        )
    }

    var errors by remember(tempUiSettings) {
        mutableStateOf(tempUiSettings.settings.mapValues { _ -> false })
    }

    val scrollState = rememberScrollState()
    Column(Modifier.scrollable(scrollState, orientation = Orientation.Vertical)) {
        tempUiSettings.settings.forEach { (k, v) ->
            SubmittableNumInput(
                error = errors[k] ?: false,
                value = v,
                onUpdate = { tempUiSettings = tempUiSettings.updateSetting(k to it) })
        }
        Button(onClick = {
            when (val x = tempUiSettings.toUISettings()) {
                is TempUISettings.ToUISettingsResult.Ok -> onSubmit(uiSettings.mergeSettings(x.settings))
                is TempUISettings.ToUISettingsResult.Error -> {
                    errors = errors + x.errors.map { v -> v to true }.toMap()
                }
            }
        }) {
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
    error: Boolean = false,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TextField(
            isError = error,
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
            onUpdate = { onUpdate(colourSettings.copy(backgroundColour = it)) })
        ColourPicker(
            color = colourSettings.shadowColour,
            onUpdate = { onUpdate(colourSettings.copy(shadowColour = it)) })
        ColourPicker(
            color = colourSettings.LColour,
            onUpdate = { onUpdate(colourSettings.copy(LColour = it)) })
        ColourPicker(
            color = colourSettings.ZColour,
            onUpdate = { onUpdate(colourSettings.copy(ZColour = it)) })
        ColourPicker(
            color = colourSettings.SColour,
            onUpdate = { onUpdate(colourSettings.copy(SColour = it)) })
        ColourPicker(
            color = colourSettings.TColour,
            onUpdate = { onUpdate(colourSettings.copy(TColour = it)) })
        ColourPicker(
            color = colourSettings.OColour,
            onUpdate = { onUpdate(colourSettings.copy(OColour = it)) })
        ColourPicker(
            color = colourSettings.JColour,
            onUpdate = { onUpdate(colourSettings.copy(JColour = it)) })
        ColourPicker(
            color = colourSettings.IColour,
            onUpdate = { onUpdate(colourSettings.copy(IColour = it)) })
        Button(onClick = onDelete) {
            Text("X")
        }
    }
}

@Composable
fun ColourPicker(color: Color, modifier: Modifier = Modifier, onUpdate: (Color) -> Unit = {}) {

    val c = Canvas(modifier = modifier)
    {
        val radius = size.minDimension / 2

        val hueBrush = ShaderBrush(
            SweepGradientShader(
                center,
                listOf(
                    Color.Red,
                    Color.Magenta,
                    Color.Blue,
                    Color.Cyan,
                    Color.Green,
                    Color.Yellow,
                    Color.Red
                ),
                listOf(0.000f, 0.166f, 0.333f, 0.499f, 0.666f, 0.833f, 0.999f)
            )
        )

        val satBrush = ShaderBrush(
            RadialGradientShader(
                center,
                radius,
                listOf(Color.White, Color(0x00FFFFFF)),
                tileMode = TileMode.Clamp
            )
        )

        val valBrush = ShaderBrush(
            LinearGradientShader(
                Offset(0F, 0F),
                Offset(size.width, 0F),
                colors = listOf(Color.Black, Color.White),
                tileMode = TileMode.Clamp
            )
        )

        drawCircle(hueBrush, radius, center)
        drawCircle(satBrush, radius, center)
        drawRect(valBrush, Offset(0F, 0F), Size(size.width, size.height / 2))
    }

}

@Preview
@Composable
fun ColourPickerPreview() {
    ColourPicker(color = Color.White, modifier = Modifier.size(50.dp), onUpdate = {})
}