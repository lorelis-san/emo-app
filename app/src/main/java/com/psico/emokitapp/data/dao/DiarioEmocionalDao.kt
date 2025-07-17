package com.psico.emokitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.psico.emokitapp.data.entities.DiarioEmocional
import java.util.Date
@Dao
interface DiarioEmocionalDao {

    @Query("SELECT * FROM diario_emocional ORDER BY timestamp DESC")
    suspend fun getAllEntradas(): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUltimasReflexiones(limit: Int): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE fecha >= :fechaInicio AND fecha < :fechaFin ORDER BY timestamp DESC")
    suspend fun getEntradasPorRango(fechaInicio: Date, fechaFin: Date): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE fecha = :fecha ORDER BY timestamp DESC")
    suspend fun getEntradasPorFecha(fecha: Date): List<DiarioEmocional>

    @Query("SELECT COUNT(*) FROM diario_emocional WHERE fecha = :fecha")
    suspend fun contarEntradasPorFecha(fecha: Date): Int

    @Insert
    suspend fun insertEntrada(entrada: DiarioEmocional)

    @Update
    suspend fun updateEntrada(entrada: DiarioEmocional)

    @Delete
    suspend fun deleteEntrada(entrada: DiarioEmocional)
}