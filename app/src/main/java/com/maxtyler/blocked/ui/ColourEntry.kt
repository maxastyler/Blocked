package com.maxtyler.blocked.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ColourEntry() {
    Box(Modifier.background(color = Color.Black)) {
        Text("Hi")
    }
}

@Preview
@Composable
fun ColourEntryPreview() {
    ColourEntry()
}