package com.psico.emokitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psico.emokitapp.data.entities.ActividadCompletada
import java.util.Date

@Dao
interface ActividadCompletadaDao {
    @Query("SELECT * FROM actividades_completadas WHERE usuarioId = :usuarioId ORDER BY fechaCompletada DESC")
    suspend fun getActividadesCompletadas(usuarioId: Int): List<ActividadCompletada>

    @Query("SELECT * FROM actividades_completadas WHERE usuarioId = :usuarioId AND DATE(fechaCompletada) = DATE(:fecha)")
    suspend fun getActividadesDelDia(usuarioId: Int, fecha: Date): List<ActividadCompletada>

    @Query("SELECT * FROM actividades_completadas WHERE usuarioId = :usuarioId AND fechaCompletada >= :inicioDelDia AND fechaCompletada <= :finDelDia ORDER BY fechaCompletada DESC")
    suspend fun getActividadesDelDiaRango(usuarioId: Int, inicioDelDia: Date, finDelDia: Date): List<ActividadCompletada>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividadCompletada(actividad: ActividadCompletada)

    @Query("SELECT COUNT(*) FROM actividades_completadas WHERE usuarioId = :usuarioId")
    suspend fun getTotalCompletadas(usuarioId: Int): Int
}