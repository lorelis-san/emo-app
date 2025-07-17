package com.psico.emokitapp.utils

import android.content.Context
import com.psico.emokitapp.data.entities.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun saveUserSession(usuario: Usuario) {
        prefs.edit()
            .putInt("user_id", usuario.id)  // ✅ Agregar ID
            .putString("user_nombre", usuario.nombre)
            .putString("user_correo", usuario.correo)
            .putString("user_contrasena", usuario.contrasena)
            .apply()
    }

    fun getUserSession(): Usuario? {
        val id = prefs.getInt("user_id", 0)  // ✅ Obtener ID
        val nombre = prefs.getString("user_nombre", null)
        val correo = prefs.getString("user_correo", null)
        val contrasena = prefs.getString("user_contrasena", null)

        return if (nombre != null && correo != null && contrasena != null) {
            Usuario(id = id, nombre = nombre, correo = correo, contrasena = contrasena)
        } else {
            null
        }
    }

    fun clearUserSession() {  // ✅ Cambiar nombre para consistencia
        prefs.edit().clear().apply()
    }

    // ✅ Método adicional útil
    fun isLoggedIn(): Boolean {
        return getUserSession() != null
    }
}