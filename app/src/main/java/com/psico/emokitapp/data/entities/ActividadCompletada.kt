package com.psico.emokitapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "actividades_completadas")
data class ActividadCompletada(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val actividadId: Int,
    val fechaCompletada: Date,
    val duracionRealizada: Int,
    val completada: Boolean = true
)