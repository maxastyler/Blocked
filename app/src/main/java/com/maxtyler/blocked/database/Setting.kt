package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class SettingTypeValue {
    Float, Long, Int
}

@Entity
data class Setting(
    @PrimaryKey val name: String,
    val parentKey: Int = 0,
    val type: SettingTypeValue,
    val value: String,
) {
    @TypeConverter
    fun typeValueToInt(type: SettingTypeValue) = type.ordinal

    @TypeConverter
    fun intToTypeValue(type: Int): SettingTypeValue? = SettingTypeValue.values().getOrNull(type)
}
