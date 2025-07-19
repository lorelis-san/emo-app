package com.psico.emokitapp.utils

import android.content.Context
import com.psico.emokitapp.data.entities.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NOMBRE = "user_nombre"
        private const val KEY_USER_CORREO = "user_correo"
        private const val KEY_USER_CONTRASENA = "user_contrasena"
        private const val KEY_SESSION_TIME = "session_time"
        private const val KEY_REMEMBER_SESSION = "remember_session"
    }

    fun saveUserSession(usuario: Usuario, rememberSession: Boolean = true) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, usuario.id)
            .putString(KEY_USER_NOMBRE, usuario.nombre)
            .putString(KEY_USER_CORREO, usuario.correo)
            .putString(KEY_USER_CONTRASENA, usuario.contrasena)
            .putLong(KEY_SESSION_TIME, System.currentTimeMillis())
            .putBoolean(KEY_REMEMBER_SESSION, rememberSession)
            .apply()
    }

    fun getUserSession(): Usuario? {
        return if (isLoggedIn()) {
            val id = prefs.getInt(KEY_USER_ID, 0)
            val nombre = prefs.getString(KEY_USER_NOMBRE, null)
            val correo = prefs.getString(KEY_USER_CORREO, null)
            val contrasena = prefs.getString(KEY_USER_CONTRASENA, null)

            if (nombre != null && correo != null && contrasena != null) {
                Usuario(id = id, nombre = nombre, correo = correo, contrasena = contrasena)
            } else {
                null
            }
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return false

        val rememberSession = prefs.getBoolean(KEY_REMEMBER_SESSION, true)

        // Si eligió recordar la sesión, mantenerla activa
        if (rememberSession) return true

        // Opcional: Verificar expiración de sesión (30 días)
        val sessionTime = prefs.getLong(KEY_SESSION_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000 // 30 días

        return (currentTime - sessionTime) < thirtyDaysInMillis
    }

    fun clearUserSession() {
        prefs.edit().clear().apply()
    }

    fun updateSessionTime() {
        prefs.edit()
            .putLong(KEY_SESSION_TIME, System.currentTimeMillis())
            .apply()
    }
}