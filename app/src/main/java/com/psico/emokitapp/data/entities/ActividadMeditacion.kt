package com.psico.emokitapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades_meditacion")
data class ActividadMeditacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val instrucciones: String,
    val duracionMinutos: Int,
    val tipo: String,
    val categoria: String
)