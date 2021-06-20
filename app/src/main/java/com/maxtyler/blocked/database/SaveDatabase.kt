package com.maxtyler.blocked.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.blocked.game.*
import java.util.*

class SaveConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun boardToString(value: Map<Vec2, Block>?): String? {
        return value?.let {
            it.entries.map { (k, v) -> "$k;$v" }
        }?.joinToString(separator = "~")
    }

    @TypeConverter
    fun stringToBoard(value: String?): Map<Vec2, Block>? {
        return value?.let {
            it.split("~").map {
                Log.d("Save", it)
                val split = it.split(";")
                Vec2.fromString(split[0]) to ColourBlock.fromPiece(Piece.fromString(split[1]))
            }.toMap()
        }
    }

    @TypeConverter
    fun rotationToInt(rotation: Rotation?): Int? = rotation.let { it.toInt() }

    @TypeConverter
    fun intToRotation(value: Int?): Rotation? = value?.let { Rotation.fromInt(it) }

    @TypeConverter
    fun pieceToString(piece: Piece?): String? = piece.let { it.toString() }

    @TypeConverter
    fun stringToPiece(value: String?): Piece? = value?.let { Piece.fromString(it) }

    @TypeConverter
    fun vecToString(v: Vec2?): String? = v.let { it.toString() }

    @TypeConverter
    fun stringToVec(s: String?): Vec2? = s?.let { Vec2.fromString(it) }

    @TypeConverter
    fun piecesToStrings(pieces: List<Piece>?): String? =
        pieces?.let { it.map { it.toString() }.joinToString(separator = ",") }

    @TypeConverter
    fun stringsToPieces(strings: String?): List<Piece>? =
        strings?.split(",")?.let { it.map { Piece.fromString(it) } }
}

@Database(entities = [Save::class], version = 1, exportSchema = true)
@TypeConverters(SaveConverters::class)
abstract class SaveDatabase : RoomDatabase() {

    abstract fun saveDao(): SaveDao
}