package com.psico.emokitapp.repository

import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.ActividadCompletada
import com.psico.emokitapp.data.entities.ActividadMeditacion
import java.util.Date
import kotlin.random.Random

class MeditacionRepository (private val database: EmokitDatabase) {

    suspend fun getActividadAleatoria(): ActividadMeditacion? {
        val actividades = database.actividadMeditacionDao().getAllActividades()
        return if (actividades.isNotEmpty()) {
            actividades[Random.nextInt(actividades.size)]
        } else null
    }
    suspend fun completarActividad(usuarioId: Int, actividadId: Int, duracionRealizada: Int) {
        val actividadCompletada = ActividadCompletada(
            usuarioId = usuarioId,
            actividadId = actividadId,
            fechaCompletada = Date(),
            duracionRealizada = duracionRealizada,
            completada = true
        )
        database.actividadCompletadaDao().insertActividadCompletada(actividadCompletada)
    }

    suspend fun getHistorialActividades(usuarioId: Int): List<ActividadCompletada> {
        return database.actividadCompletadaDao().getActividadesCompletadas(usuarioId)
    }

    suspend fun getActividadesHoy(usuarioId: Int): List<ActividadCompletada> {
        return database.actividadCompletadaDao().getActividadesDelDia(usuarioId, Date())
    }
}