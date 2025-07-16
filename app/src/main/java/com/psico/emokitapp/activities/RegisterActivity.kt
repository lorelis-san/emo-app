package com.psico.emokitapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.psico.emokitapp.R
import com.psico.emokitapp.data.entities.Usuario
import com.psico.emokitapp.viewmodel.UsuarioViewModel

class RegisterActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNombre = findViewById<TextInputEditText>(R.id.etRegisterNombre)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegisterUser)

        btnRegister.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(nombre = nombre, correo = email, contrasena = password)
            usuarioViewModel.insertar(usuario)

            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            finish() // Volver al login automáticamente
        }
    }
}
