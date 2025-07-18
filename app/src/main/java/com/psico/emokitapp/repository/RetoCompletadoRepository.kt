package com.psico.emokitapp.repository

import com.psico.emokitapp.data.dao.RetoCompletadoDao
import com.psico.emokitapp.data.entities.RetoCompletado

class RetoCompletadoRepository(private val dao: RetoCompletadoDao) {

    suspend fun insertar(retoCompletado: RetoCompletado) {
        dao.insertar(retoCompletado)
    }

    suspend fun obtenerRetosPorFecha(correo: String, fecha: String): List<RetoCompletado> {
        return dao.obtenerRetosCompletadosPorFecha(correo, fecha)
    }

    suspend fun obtenerTodosRetos(correo: String): List<RetoCompletado> {
        return dao.obtenerTodosRetosCompletados(correo)
    }
}
