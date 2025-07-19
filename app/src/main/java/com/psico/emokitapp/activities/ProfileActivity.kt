package com.psico.emokitapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.psico.emokitapp.R
import com.psico.emokitapp.viewmodel.UsuarioViewModel
import com.psico.emokitapp.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // ✅ Usar SessionManager consistentemente
        val sessionManager = SessionManager(this)
        val usuario = sessionManager.getUserSession()

        // ✅ Mostrar datos del usuario actual
        if (usuario != null) {
            tvUserName.text = usuario.nombre
            tvUserEmail.text = usuario.correo
        } else {
            // Si no hay sesión guardada, ir a login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        btnEditProfile.setOnClickListener {
            sessionManager.clearUserSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this::class != HomeActivity::class) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    true
                }
                R.id.nav_add -> {
                    if (this::class != HomeActivity::class) {
                        startActivity(Intent(this, DiarioEmocionalActivity::class.java))
                    }
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}