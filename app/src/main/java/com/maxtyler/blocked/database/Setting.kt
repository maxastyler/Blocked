package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SettingTypeValue {
    Float, Long, Int
}

@Entity
data class Setting(
    @PrimaryKey val name: String,
    val parentKey: Int = 0,
    val type: SettingTypeValue,
    val value: String,
)
