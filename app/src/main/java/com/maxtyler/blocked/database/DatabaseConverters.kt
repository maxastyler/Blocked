package com.maxtyler.blocked.database

import androidx.room.TypeConverter
import com.maxtyler.blocked.game.Piece
import com.maxtyler.blocked.game.Rotation
import com.maxtyler.blocked.game.Vec2
import java.util.*

class DatabaseConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun boardToString(value: Map<Vec2, Piece>?): String? {
        return value?.let {
            it.entries.map { (k, v) -> "$k;$v" }
        }?.joinToString(separator = "~")
    }

    @TypeConverter
    fun stringToBoard(value: String?): Map<Vec2, Piece>? {
        return value?.let {
            it.split("~").map {
                val split = it.split(";")
                Vec2.fromString(split[0]) to Piece.valueOf(split[1])
            }.toMap()
        }
    }

    @TypeConverter
    fun rotationToInt(rotation: Rotation?): Int? = rotation?.toInt()

    @TypeConverter
    fun intToRotation(value: Int?): Rotation? = value?.let { Rotation.fromInt(it) }

    @TypeConverter
    fun pieceToString(piece: Piece?): String? = piece?.toString()

    @TypeConverter
    fun stringToPiece(value: String?): Piece? = value?.let { Piece.valueOf(it) }

    @TypeConverter
    fun vecToString(v: Vec2?): String? = v?.toString()

    @TypeConverter
    fun stringToVec(s: String?): Vec2? = s?.let { Vec2.fromString(it) }

    @TypeConverter
    fun piecesToStrings(pieces: List<Piece>?): String? =
        pieces?.let { it.map { it.toString() }.joinToString(separator = ",") }

    @TypeConverter
    fun stringsToPieces(strings: String?): List<Piece>? =
        strings?.split(",")?.let { it.map { Piece.valueOf(it) } }

    @TypeConverter
    fun typeValueToInt(type: SettingTypeValue) = type.ordinal

    @TypeConverter
    fun intToTypeValue(type: Int): SettingTypeValue? = SettingTypeValue.values().getOrNull(type)

}