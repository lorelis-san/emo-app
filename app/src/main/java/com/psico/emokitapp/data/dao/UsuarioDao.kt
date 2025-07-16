package com.psico.emokitapp.data.dao

import androidx.room.*
import com.psico.emokitapp.data.entities.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    @Delete
    suspend fun eliminar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    fun obtenerUsuarioPorCorreo(correo: String): Usuario

}
