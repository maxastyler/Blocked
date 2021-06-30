package com.maxtyler.blocked.database

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter

class ColourSettingsConverters {
    @TypeConverter
    fun colourToLong(colour: Color?): ULong? = colour?.value

    @TypeConverter
    fun longToColour(value: ULong?): Color? = value?.let {Color(it)}
}