package com.psico.emokitapp.activities
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.psico.emokitapp.R
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity() {
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
            startActivity(Intent(this, RetosDiariosActivity::class.java))
        }

        cardMeditaciones.setOnClickListener {
            startActivity(Intent(this, MeditacionesActivity::class.java))
        }

        cardProgreso.setOnClickListener {
            startActivity(Intent(this, ProgresoActivity::class.java))
        }



        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
//                R.id.nav_add -> {
//                    if (this !is AddActivity) {
//                        startActivity(Intent(this, AddActivity::class.java))
//                        finish()
//                    }
//                    true
//                }

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