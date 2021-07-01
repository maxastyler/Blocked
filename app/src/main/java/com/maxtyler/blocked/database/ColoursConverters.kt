package com.maxtyler.blocked.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

class ColoursConverters {
    @TypeConverter
    fun colourToLong(colour: Color?): Int? = colour?.toArgb()

    @TypeConverter
    fun longToColour(value: Int?): Color? = value?.let { Color(it) }
}