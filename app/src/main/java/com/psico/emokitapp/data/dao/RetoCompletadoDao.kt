package com.psico.emokitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psico.emokitapp.data.entities.RetoCompletado

@Dao
interface RetoCompletadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(retoCompletado: RetoCompletado)

    @Query("SELECT * FROM retos_completados WHERE usuarioCorreo = :correo AND fecha = :fecha")
    suspend fun obtenerRetosPorUsuarioYFecha(correo: String, fecha: String): List<RetoCompletado>

    @Query("SELECT * FROM retos_completados WHERE usuarioCorreo = :correo AND fecha = :fecha")
    suspend fun obtenerRetosCompletadosPorFecha(correo: String, fecha: String): List<RetoCompletado>

    @Query("SELECT * FROM retos_completados WHERE usuarioCorreo = :correo")
    suspend fun obtenerTodosRetosCompletados(correo: String): List<RetoCompletado>
}
