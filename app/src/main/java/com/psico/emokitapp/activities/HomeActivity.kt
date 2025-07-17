package com.psico.emokitapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationBarView
import com.psico.emokitapp.R
import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.EmotionalState
import com.psico.emokitapp.data.entities.Usuario
import com.psico.emokitapp.utils.SessionManager
import com.psico.emokitapp.views.EmotionalStateChart
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
class HomeActivity : AppCompatActivity() {

    private lateinit var database: EmokitDatabase
    private lateinit var chartEstadoEmocional: EmotionalStateChart
    private lateinit var imgEmocionPrincipal: ImageView
    private lateinit var tvDescripcionEstado: TextView
    private lateinit var tvPorcentajeEmocional: TextView

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

        initializeEmotionalChart()
    }

    private fun initializeEmotionalChart() {
        database = EmokitDatabase.getDatabase(this)
        chartEstadoEmocional = findViewById(R.id.chartEstadoEmocional)
        imgEmocionPrincipal = findViewById(R.id.imgEmocionPrincipal)
        tvDescripcionEstado = findViewById(R.id.tvDescripcionEstado)
        tvPorcentajeEmocional = findViewById(R.id.tvPorcentajeEmocional)

        loadTodayEmotionalState()
    }

    private fun loadTodayEmotionalState() {
        lifecycleScope.launch {
            val todayStart = getStartOfDay()
            val todayEnd = getEndOfDay()

            val todayEntries = database.diarioEmocionalDao().getEntradasPorRango(todayStart, todayEnd)
            val emotionalState = calculateEmotionalState(todayEntries.map { it.emocion })

            updateEmotionalChart(emotionalState)
        }
    }

    private fun calculateEmotionalState(emotions: List<String>): EmotionalState {
        val emotionCounts = emotions.groupingBy { it }.eachCount()
        val total = emotions.size

        val dominantEmotion = emotionCounts.maxByOrNull { it.value }
        val dominantPercentage = if (total > 0) {
            (dominantEmotion?.value?.toFloat() ?: 0f) / total * 100f
        } else 0f

        return EmotionalState(
            emotions = emotionCounts,
            dominantEmotion = dominantEmotion?.key,
            dominantPercentage = dominantPercentage
        )
    }

    private fun updateEmotionalChart(emotionalState: EmotionalState) {
        chartEstadoEmocional.setEmotionData(emotionalState.emotions)

        emotionalState.dominantEmotion?.let { emotion ->
            imgEmocionPrincipal.setImageResource(EmotionalState.getEmotionIcon(emotion))
            tvDescripcionEstado.text = EmotionalState.getEmotionDescription(emotion)
            tvPorcentajeEmocional.text = "${emotionalState.dominantPercentage.toInt()}%"
        } ?: run {
            imgEmocionPrincipal.setImageResource(R.drawable.ic_serious)
            tvDescripcionEstado.text = "Sin registros hoy"
            tvPorcentajeEmocional.text = "0%"
        }
    }

    private fun getStartOfDay(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun getEndOfDay(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
    }

    override fun onResume() {
        super.onResume()
        if (::database.isInitialized) {
            loadTodayEmotionalState()
        }
    }
}
