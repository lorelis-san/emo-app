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

class LoginActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etLoginEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginUser)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            usuarioViewModel.verificarLogin(email, password) { correcto ->
                runOnUiThread {
                    if (correcto) {
                        Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                        // Descomenta cuando tengas HomeActivity
                         startActivity(Intent(this, HomeActivity::class.java))

                        val sharedPref = getSharedPreferences("EmokitPreferences", MODE_PRIVATE)
                        sharedPref.edit().putString("user_email", email).apply()
                        // finish()
                    } else {
                        Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }




    }
}
