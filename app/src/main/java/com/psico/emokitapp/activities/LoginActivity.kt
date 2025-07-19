package com.psico.emokitapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.psico.emokitapp.R
import com.psico.emokitapp.viewmodel.UsuarioViewModel
import com.psico.emokitapp.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etLoginEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginUser)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Ingresa un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Iniciando..."

            usuarioViewModel.obtenerUsuarioPorCorreo(email) { usuario ->
                runOnUiThread {
                    // Restaurar botón
                    btnLogin.isEnabled = true
                    btnLogin.text = "Iniciar Sesión"

                    if (usuario != null && usuario.contrasena == password) {
                        sessionManager.saveUserSession(usuario, rememberSession = true)

                        Toast.makeText(this@LoginActivity, "¡Bienvenido ${usuario.nombre}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        if (usuario == null) {
                            Toast.makeText(this@LoginActivity, "El email no está registrado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}