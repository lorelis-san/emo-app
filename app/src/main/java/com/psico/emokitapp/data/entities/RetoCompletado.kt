package com.psico.emokitapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retos_completados")
data class RetoCompletado(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioCorreo: String,
    val retoTitulo: String,
    val fecha: String // formato "yyyy-MM-dd"
)
