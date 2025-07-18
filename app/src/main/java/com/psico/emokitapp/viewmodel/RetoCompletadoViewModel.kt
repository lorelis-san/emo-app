package com.psico.emokitapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psico.emokitapp.data.entities.RetoCompletado
import com.psico.emokitapp.repository.RetoCompletadoRepository
import com.psico.emokitapp.data.EmokitDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RetoCompletadoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RetoCompletadoRepository

    init {
        val dao = EmokitDatabase.getDatabase(application).retoCompletadoDao()
        repository = RetoCompletadoRepository(dao)

    }

    fun insertarRetoCompletado(retoCompletado: RetoCompletado) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertar(retoCompletado)
        }
    }

    fun obtenerRetosPorFecha(
        correo: String,
        fecha: String,
        onResult: (List<RetoCompletado>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val retos = repository.obtenerRetosPorFecha(correo, fecha)
            onResult(retos)
        }
    }

    fun obtenerTodosRetos(
        correo: String,
        onResult: (List<RetoCompletado>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val retos = repository.obtenerTodosRetos(correo)
            onResult(retos)
        }
    }
}
