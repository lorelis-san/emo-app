package com.psico.emokitapp.repository

import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.ActividadCompletada
import com.psico.emokitapp.data.entities.ActividadMeditacion
import java.util.Calendar
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
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val inicioDelDia = calendar.time

        // Final del día (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val finDelDia = calendar.time

        return database.actividadCompletadaDao().getActividadesDelDiaRango(usuarioId, inicioDelDia, finDelDia)
    }
}