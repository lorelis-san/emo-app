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
import com.psico.emokitapp.utils.SessionManager
import com.psico.emokitapp.views.EmotionalStateChart
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var database: EmokitDatabase
    private lateinit var chartEstadoEmocional: EmotionalStateChart
    private lateinit var imgEmocionPrincipal: ImageView
    private lateinit var tvDescripcionEstado: TextView
    private lateinit var tvPorcentajeEmocional: TextView
    private lateinit var tvMotivacional: TextView

    private lateinit var sessionManager: SessionManager
    private var userEmail: String? = null

    private val frasesMotivacionales = arrayOf(
        "Haz de hoy tu día.",
        "Cada día es una nueva oportunidad para crecer.",
        "Tu actitud determina tu altitud.",
        "El éxito es la suma de pequeños esfuerzos repetidos día tras día.",
        "Cree en ti mismo y todo será posible.",
        "Hoy es el primer día del resto de tu vida.",
        "No esperes el momento perfecto, toma el momento y hazlo perfecto.",
        "Tu única limitación eres tú mismo.",
        "Los sueños no funcionan a menos que tú lo hagas.",
        "Cada paso cuenta hacia tu meta.",
        "La felicidad no es un destino, es una forma de vida.",
        "Transforma tus heridas en sabiduría.",
        "El progreso, no la perfección.",
        "Eres más fuerte de lo que crees.",
        "Hoy elige ser feliz.",
        "Tu potencial es infinito.",
        "Abraza el cambio, es donde está el crecimiento.",
        "Pequeños pasos cada día llevan a grandes cambios cada año.",
        "Tu mentalidad es todo.",
        "Haz que suceda.",
        "El mejor momento para plantar un árbol fue hace 20 años, el segundo mejor momento es ahora.",
        "No cuentes los días, haz que los días cuenten.",
        "La vida comienza fuera de tu zona de confort.",
        "Eres el autor de tu propia historia.",
        "Enfócate en el progreso, no en la perfección.",
        "Tu energía es contagiosa, úsala sabiamente.",
        "Cada amanecer trae nuevas posibilidades.",
        "Confía en el proceso de la vida.",
        "Lo que no te desafía, no te cambia.",
        "Hoy es un buen día para tener un buen día.",
        "La persistencia es el camino del éxito.",
        "Convierte tus obstáculos en oportunidades.",
        "El único fracaso es no intentarlo.",
        "Tu momento es ahora.",
        "Crea tu propia suerte trabajando duro.",
        "Los límites solo existen en tu mente.",
        "Cada día es una página en blanco de tu historia.",
        "La magia sucede fuera de tu zona de confort.",
        "Eres capaz de cosas increíbles.",
        "Hoy puedes comenzar de nuevo."
    )

    private fun obtenerUsuarioActualEmail(): String? {
        sessionManager = SessionManager(this)
        val usuario = sessionManager.getUserSession()
        return usuario?.correo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userEmail = obtenerUsuarioActualEmail()
        if (userEmail == null) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar la frase motivacional
        tvMotivacional = findViewById(R.id.tvMotivacional)
        setDailyMotivationalMessage()

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
            val correo = userEmail
            if (correo != null) {
                val intent = Intent(this, RetosDiariosActivity::class.java)
                intent.putExtra("user_email", correo)
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
                R.id.nav_home -> {
                    if (this::class != HomeActivity::class) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, DiarioEmocionalActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    if (this::class != ProfileActivity::class) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }

        initializeEmotionalChart()
    }

    private fun setDailyMotivationalMessage() {
        // Usar la fecha actual como semilla para que sea consistente durante todo el día
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        // Crear una semilla única para cada día
        val seed = (year * 1000 + dayOfYear).toLong()
        val random = Random(seed)

        // Seleccionar una frase basada en el día
        val fraseDelDia = frasesMotivacionales[random.nextInt(frasesMotivacionales.size)]
        tvMotivacional.text = fraseDelDia
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

            val correo = userEmail
            if (correo != null) {
                val todayEntries = database.diarioEmocionalDao().getEntradasPorUsuarioYRango(
                    correo, todayStart, todayEnd
                )
                val emotionalState = calculateEmotionalState(todayEntries.map { it.emocion })
                updateEmotionalChart(emotionalState)
            } else {
                Toast.makeText(this@HomeActivity, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            }
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
        // Actualizar la frase motivacional al resumir la actividad
        if (::tvMotivacional.isInitialized) {
            setDailyMotivationalMessage()
        }
    }
}