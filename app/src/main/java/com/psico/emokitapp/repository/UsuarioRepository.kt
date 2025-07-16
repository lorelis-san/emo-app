package com.psico.emokitapp.repository

import com.psico.emokitapp.data.dao.UsuarioDao
import com.psico.emokitapp.data.entities.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun insertar(usuario: Usuario) {
        usuarioDao.insertar(usuario)
    }

    suspend fun obtenerTodos(): List<Usuario> {
        return usuarioDao.obtenerTodos()
    }

    suspend fun obtenerPorCorreo(correo: String): Usuario? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    suspend fun eliminar(usuario: Usuario) {
        usuarioDao.eliminar(usuario)
    }


}
