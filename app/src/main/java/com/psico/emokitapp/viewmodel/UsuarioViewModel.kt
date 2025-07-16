package com.psico.emokitapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.Usuario
import com.psico.emokitapp.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository

    init {
        val db = Room.databaseBuilder(
            application,
            EmokitDatabase::class.java,
            "emokit_db"
        ).build()
        repository = UsuarioRepository(db.usuarioDao())

    }

    fun insertar(usuario: Usuario) {
        viewModelScope.launch {
            repository.insertar(usuario)
        }
    }

    fun verificarLogin(correo: String, contrasena: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val usuario = repository.obtenerPorCorreo(correo)
            val correcto = usuario != null && usuario.contrasena == contrasena
            onResult(correcto)
        }
    }


    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        return repository.obtenerPorCorreo(correo)
    }



}
