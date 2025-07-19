package com.psico.emokitapp.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.psico.emokitapp.data.entities.ActividadMeditacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            poblarDatosIniciales()
        }
    }

    private suspend fun poblarDatosIniciales() {
        val database = EmokitDatabase.getDatabase(context)
        val actividadDao = database.actividadMeditacionDao()

        if (actividadDao.getCount() == 0) {
            val actividades = listOf(
                ActividadMeditacion(
                    titulo = "Respiración Consciente",
                    descripcion = "Enfócate en tu respiración para calmar la mente",
                    instrucciones = "Siéntate cómodamente, cierra los ojos y concentra tu atención en tu respiración natural. Cuenta cada inhalación y exhalación del 1 al 10.",
                    duracionMinutos = 1,
                    tipo = "mindfulness",
                    categoria = "ansiedad"
                ),
                ActividadMeditacion(
                    titulo = "Escaneo Corporal",
                    descripcion = "Reconecta con tu cuerpo a través de la atención plena",
                    instrucciones = "Recuesta o siéntate cómodamente. Lleva tu atención a cada parte de tu cuerpo, desde los pies hasta la cabeza, notando las sensaciones.",
                    duracionMinutos = 2,
                    tipo = "mindfulness",
                    categoria = "estres"
                ),
                ActividadMeditacion(
                    titulo = "Observación de Pensamientos",
                    descripcion = "Observa tus pensamientos sin juzgar",
                    instrucciones = "Siéntate en silencio y observa tus pensamientos como nubes que pasan por el cielo. No los juzgues, solo obsérvalos.",
                    duracionMinutos = 1,
                    tipo = "mindfulness",
                    categoria = "general"
                ),

                ActividadMeditacion(
                    titulo = "Diario de Gratitud",
                    descripcion = "Escribe tres cosas por las que te sientes agradecido",
                    instrucciones = "Toma papel y lápiz. Escribe tres cosas específicas por las que te sientes agradecido hoy. Pueden ser pequeñas o grandes.",
                    duracionMinutos = 1,
                    tipo = "escritura",
                    categoria = "autoestima"
                ),
                ActividadMeditacion(
                    titulo = "Carta a tu Yo Futuro",
                    descripcion = "Escribe una carta motivacional para ti mismo",
                    instrucciones = "Escribe una carta breve a tu yo del futuro. Incluye tus esperanzas, metas y palabras de aliento.",
                    duracionMinutos = 2,
                    tipo = "escritura",
                    categoria = "autoestima"
                ),

                ActividadMeditacion(
                    titulo = "Relajación Progresiva",
                    descripcion = "Tensa y relaja cada grupo muscular",
                    instrucciones = "Tensa los músculos de tus pies por 5 segundos, luego relaja. Continúa hacia arriba: pantorrillas, muslos, abdomen, brazos, hasta llegar a la cabeza.",
                    duracionMinutos = 2,
                    tipo = "relajacion",
                    categoria = "estres"
                ),
                ActividadMeditacion(
                    titulo = "Visualización Positiva",
                    descripcion = "Visualiza un lugar que te traiga paz",
                    instrucciones = "Cierra los ojos e imagina un lugar donde te sientes completamente seguro y en paz. Utiliza todos tus sentidos para hacer la visualización más vívida.",
                    duracionMinutos = 1,
                    tipo = "relajacion",
                    categoria = "ansiedad"
                ),

                ActividadMeditacion(
                    titulo = "Respiración 4-7-8",
                    descripcion = "Técnica de respiración para reducir la ansiedad",
                    instrucciones = "Inhala por 4 segundos, mantén el aire por 7 segundos, exhala por 8 segundos. Repite este ciclo.",
                    duracionMinutos = 1,
                    tipo = "respiracion",
                    categoria = "ansiedad"
                ),
                ActividadMeditacion(
                    titulo = "Respiración Abdominal",
                    descripcion = "Respira profundamente usando el diafragma",
                    instrucciones = "Pon una mano en el pecho y otra en el abdomen. Respira lentamente, asegurándote de que solo se mueva la mano del abdomen.",
                    duracionMinutos = 1,
                    tipo = "respiracion",
                    categoria = "estres"
                )
            )
            actividadDao.insertAllActividades(actividades)
        }
    }
}