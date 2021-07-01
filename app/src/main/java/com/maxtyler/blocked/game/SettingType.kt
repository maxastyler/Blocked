package com.maxtyler.blocked.game

sealed class SettingType<T> {
    abstract val name: String
    abstract val default: T
    abstract val value: T

    data class IntSetting(
        override val name: String,
        override val default: Int,
        override val value: Int,
        val minimum: Int? = null,
        val maximum: Int? = null
    ) : SettingType<Int>()

    data class FloatSetting(
        override val name: String,
        override val default: Float,
        override val value: Float,
        val minimum: Float? = null,
        val maximum: Float? = null,
    ) : SettingType<Float>()

    data class LongSetting(
        override val name: String,
        override val default: Long,
        override val value: Long,
        val minimum: Long? = null,
        val maximum: Long? = null,
    ) : SettingType<Long>()
}
