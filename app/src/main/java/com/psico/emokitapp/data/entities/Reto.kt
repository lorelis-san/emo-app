package com.psico.emokitapp.data.entities

data class Reto(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val completado: Boolean,
    val recompensa: String
)
