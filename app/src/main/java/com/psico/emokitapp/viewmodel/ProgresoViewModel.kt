package com.psico.emokitapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.RetoCompletado
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgresoViewModel(application: Application) : AndroidViewModel(application) {

    private val retoCompletadoDao = EmokitDatabase.getDatabase(application).retoCompletadoDao()

    fun getRetosHoy(correo: String): LiveData<List<RetoCompletado>> = liveData(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Calendar.getInstance().time)
        emit(retoCompletadoDao.obtenerRetosPorUsuarioYFecha(correo, today))
    }

    fun getProgresoSemana(correo: String): LiveData<Map<String, Int>> = liveData(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        val progreso = mutableMapOf<String, Int>()

        repeat(7) {
            val fecha = sdf.format(calendar.time)
            val dia = dayFormat.format(calendar.time)
            val count = retoCompletadoDao.obtenerRetosPorUsuarioYFecha(correo, fecha).size
            progreso[dia] = count
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        emit(progreso)
    }
}
