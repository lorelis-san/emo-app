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

    @Query("SELECT * FROM diario_emocional WHERE usuario_correo = :usuarioCorreo ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUltimasReflexiones(usuarioCorreo: String, limit: Int): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE usuario_correo = :usuarioCorreo AND fecha >= :fechaInicio AND fecha <= :fechaFin ORDER BY timestamp DESC")
    suspend fun getEntradasPorUsuarioYRango(
        usuarioCorreo: String,
        fechaInicio: Date,
        fechaFin: Date
    ): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE usuario_correo = :usuarioCorreo AND fecha = :fecha")
    suspend fun getEntradasPorUsuarioYFecha(
        usuarioCorreo: String,
        fecha: Date
    ): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE usuario_correo = :usuarioCorreo AND fecha >= :fechaInicio AND fecha < :fechaFin ORDER BY timestamp DESC")
    suspend fun getEntradasPorRango(
        usuarioCorreo: String,
        fechaInicio: Date,
        fechaFin: Date
    ): List<DiarioEmocional>

    @Query("SELECT * FROM diario_emocional WHERE usuario_correo = :usuarioCorreo AND fecha = :fecha ORDER BY timestamp DESC")
    suspend fun getEntradasPorFecha(
        usuarioCorreo: String,
        fecha: Date
    ): List<DiarioEmocional>

    @Query("SELECT COUNT(*) FROM diario_emocional WHERE usuario_correo = :usuarioCorreo AND fecha = :fecha")
    suspend fun contarEntradasPorFecha(
        usuarioCorreo: String,
        fecha: Date
    ): Int

    @Insert
    suspend fun insertEntrada(entrada: DiarioEmocional)

    @Update
    suspend fun updateEntrada(entrada: DiarioEmocional)

    @Delete
    suspend fun deleteEntrada(entrada: DiarioEmocional)
}
