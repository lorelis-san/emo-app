package com.psico.emokitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "diario_emocional")
@TypeConverters(DateConverter::class)
data class DiarioEmocional (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val emocion: String,
    val descripcion: String,
    val fecha: Date,
    val timestamp: Date = Date(),
    @ColumnInfo(name = "usuario_correo")
    val usuarioCorreo: String
)
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}