package com.psico.emokitapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationBarView
import com.psico.emokitapp.R
import com.psico.emokitapp.data.entities.Usuario
import com.psico.emokitapp.utils.SessionManager

class HomeActivity : AppCompatActivity() {

    private fun obtenerUsuarioActual(): Usuario? {
        val sessionManager = SessionManager(this)
        return sessionManager.getUserSession()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED

        val cardDiario = findViewById<MaterialCardView>(R.id.cardDiario)
        val cardRetos = findViewById<MaterialCardView>(R.id.cardRetos)
        val cardMeditaciones = findViewById<MaterialCardView>(R.id.cardMeditaciones)
        val cardProgreso = findViewById<MaterialCardView>(R.id.cardProgreso)

        cardDiario.setOnClickListener {
            startActivity(Intent(this, DiarioEmocionalActivity::class.java))
        }

        cardRetos.setOnClickListener {
            val usuario = obtenerUsuarioActual()
            if (usuario != null) {
                val intent = Intent(this, RetosDiariosActivity::class.java)
                intent.putExtra("user_email", usuario.correo)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            }
        }

        cardMeditaciones.setOnClickListener {
            startActivity(Intent(this, MeditacionesActivity::class.java))
        }

        cardProgreso.setOnClickListener {
            startActivity(Intent(this, ProgresoActivity::class.java))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_profile -> {
                    if (this::class != ProfileActivity::class) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
