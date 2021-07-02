package com.maxtyler.blocked.game

sealed class SettingType<T> {
    abstract val name: String
    abstract val default: T
    abstract val value: T
    abstract fun verifyString(s: String): SettingType<T>?
    abstract fun toValue(s: String): T?
    abstract fun addValue(v: T): SettingType<T>

    sealed class NumberSettingType<T : Comparable<T>> : SettingType<T>() {

        abstract val minimum: T?
        abstract val maximum: T?
        override fun verifyString(s: String): SettingType<T>? = toValue(s)?.let { v ->
            val minSat = minimum?.let { v >= it } ?: true
            val maxSat = maximum?.let { v <= it } ?: true
            if (minSat && maxSat) {
                this.addValue(v)
            } else {
                null
            }
        }
    }

    data class IntSetting(
        override val name: String,
        override val default: Int,
        override val value: Int,
        override val minimum: Int? = null,
        override val maximum: Int? = null
    ) : NumberSettingType<Int>() {
        override fun toValue(s: String): Int? = s.toIntOrNull()
        override fun addValue(v: Int): SettingType<Int> = this.copy(value = v)
    }

    data class FloatSetting(
        override val name: String,
        override val default: Float,
        override val value: Float,
        override val minimum: Float? = null,
        override val maximum: Float? = null,
    ) : NumberSettingType<Float>() {
        override fun toValue(s: String): Float? = s.toFloatOrNull()
        override fun addValue(v: Float): SettingType<Float> = this.copy(value = v)
    }

    data class LongSetting(
        override val name: String,
        override val default: Long,
        override val value: Long,
        override val minimum: Long? = null,
        override val maximum: Long? = null,
    ) : NumberSettingType<Long>() {
        override fun toValue(s: String): Long? = s.toLongOrNull()
        override fun addValue(v: Long): SettingType<Long> = this.copy(value = v)
    }
}
