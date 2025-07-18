package com.psico.emokitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psico.emokitapp.data.entities.ActividadMeditacion

@Dao
interface ActividadMeditacionDao {
    @Query("SELECT * FROM actividades_meditacion")
    suspend fun getAllActividades(): List<ActividadMeditacion>

    @Query("SELECT * FROM actividades_meditacion WHERE id = :id")
    suspend fun getActividadById(id: Int): ActividadMeditacion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: ActividadMeditacion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActividades(actividades: List<ActividadMeditacion>)

    @Query("SELECT COUNT(*) FROM actividades_meditacion")
    suspend fun getCount(): Int
}