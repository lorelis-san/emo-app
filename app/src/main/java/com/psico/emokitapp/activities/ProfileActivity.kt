package com.psico.emokitapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.psico.emokitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import androidx.activity.viewModels
import com.psico.emokitapp.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)

        val sharedPref = getSharedPreferences("EmokitPreferences", MODE_PRIVATE)
        val userEmail = sharedPref.getString("user_email", null)

        lifecycleScope.launch {
            userEmail?.let { email ->
                val usuario = usuarioViewModel.obtenerUsuarioPorCorreo(email)
                usuario?.let {
                    tvUserName.text = it.nombre
                    tvUserEmail.text = it.correo
                }
            }
        }

        btnEditProfile.setOnClickListener {
            // Cerrar sesión
            val editor = sharedPref.edit()
            editor.remove("user_email")
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Configuración de la barra de navegación
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this::class != HomeActivity::class) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    true
                }
                else -> false
            }
        }
    }
}
