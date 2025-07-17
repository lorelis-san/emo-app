package com.psico.emokitapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.psico.emokitapp.adapters.RetosAdapter
import com.psico.emokitapp.data.entities.Reto
import com.psico.emokitapp.databinding.ActivityRetosDiariosBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class RetosDiariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRetosDiariosBinding
    private lateinit var retosAdapter: RetosAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userEmail: String

    private var completedChallenges = 0
    private val totalDailyChallenges = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetosDiariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("user_email") ?: "unknown_user"
        sharedPreferences = getSharedPreferences("EmokitPrefs_${userEmail}", Context.MODE_PRIVATE)

        setupUI()
        setupRetosRecyclerView()
        setupClickListeners()
    }

    private fun setupUI() {
        updateProgress()
    }

    private fun setupRetosRecyclerView() {
        val dailyRetos = getDailyRetos().toMutableList()

        retosAdapter = RetosAdapter(dailyRetos) { reto ->
            toggleRetoCompletion(reto)
        }

        binding.retosRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.retosRecyclerView.adapter = retosAdapter
    }

    private fun getDailyRetos(): List<Reto> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val savedDate = sharedPreferences.getString("last_retos_date", "")

        return if (savedDate != today) {
            val newRetos = generateDailyRetos()
            saveDailyRetos(newRetos, today)
            newRetos
        } else {
            loadSavedRetos()
        }
    }

    private fun generateDailyRetos(): List<Reto> {
        val allRetos = getAllRetos()
        return allRetos.shuffled(Random(System.currentTimeMillis()))
            .take(totalDailyChallenges)
            .mapIndexed { index, reto -> reto.copy(id = index + 1) }
    }

    private fun getAllRetos(): List<Reto> = listOf(
        Reto(1, "Practica la respiración profunda", "Realiza 5 respiraciones profundas cuando te sientas estresado", false, "🏅"),
        Reto(2, "Escucha sin interrumpir", "Mantén una conversación sin interrumpir a la otra persona", false, "🥇"),
        Reto(3, "Expresa gratitud", "Agradece sinceramente a alguien que te haya ayudado hoy", false, "⭐"),
        Reto(4, "Identifica una emoción", "Reconoce y nombra una emoción que sientes en este momento", false, "🏆"),
        Reto(5, "Pausa antes de reaccionar", "Tómate 3 segundos antes de responder en una situación tensa", false, "🥈"),
        Reto(6, "Practica la autocompasión", "Háblate a ti mismo con amabilidad cuando cometas un error", false, "🌟"),
        Reto(7, "Observa sin juzgar", "Observa tus pensamientos sin juzgarlos durante 5 minutos", false, "🏅"),
        Reto(8, "Haz una actividad relajante", "Dedica 10 minutos a algo que te calme y te dé paz", false, "🥇"),
        Reto(9, "Conecta con la naturaleza", "Pasa al menos 5 minutos observando algo natural", false, "⭐"),
        Reto(10, "Practica la empatía", "Intenta entender el punto de vista de alguien más", false, "🏆"),
        Reto(11, "Acepta una emoción difícil", "Reconoce una emoción negativa sin intentar cambiarla", false, "🥈"),
        Reto(12, "Sonríe genuinamente", "Ofrece una sonrisa sincera a alguien que encuentres", false, "🌟"),
        Reto(13, "Haz una pausa mindful", "Detente y observa conscientemente tu entorno por 2 minutos", false, "🏅"),
        Reto(14, "Practica el perdón", "Perdona a alguien (incluyéndote a ti mismo) por algo pequeño", false, "🥇"),
        Reto(15, "Valida tus emociones", "Acepta que todas tus emociones son válidas y tienen un propósito", false, "⭐"),
        Reto(16, "Haz algo por ti mismo", "Dedica tiempo a una actividad que realmente disfrutes", false, "🏆")
    )

    private fun saveDailyRetos(retos: List<Reto>, date: String) {
        val editor = sharedPreferences.edit()
        editor.putString("last_retos_date", date)
        retos.forEachIndexed { index, reto ->
            editor.putString("reto_${index}_titulo", reto.titulo)
            editor.putString("reto_${index}_descripcion", reto.descripcion)
            editor.putString("reto_${index}_recompensa", reto.recompensa)
            editor.putBoolean("reto_${index}_completado", reto.completado)
        }
        editor.apply()
    }

    private fun loadSavedRetos(): List<Reto> {
        val retos = mutableListOf<Reto>()
        for (i in 0 until totalDailyChallenges) {
            val titulo = sharedPreferences.getString("reto_${i}_titulo", "") ?: ""
            val descripcion = sharedPreferences.getString("reto_${i}_descripcion", "") ?: ""
            val recompensa = sharedPreferences.getString("reto_${i}_recompensa", "🏅") ?: "🏅"
            val completado = sharedPreferences.getBoolean("reto_${i}_completado", false)

            if (titulo.isNotEmpty()) {
                retos.add(Reto(i + 1, titulo, descripcion, completado, recompensa))
            }
        }
        return retos
    }

    private fun toggleRetoCompletion(reto: Reto) {
        val newStatus = !reto.completado
        sharedPreferences.edit().putBoolean("reto_${reto.id - 1}_completado", newStatus).apply()

        retosAdapter.updateReto(reto.copy(completado = newStatus))
        completedChallenges += if (newStatus) 1 else -1

        if (newStatus) showRewardMessage(reto.recompensa)

        updateProgress()
    }

    private fun showRewardMessage(reward: String) {
        Toast.makeText(this, "¡Felicidades! Has ganado: $reward", Toast.LENGTH_SHORT).show()
    }

    private fun updateProgress() {
        completedChallenges = (0 until totalDailyChallenges).count {
            sharedPreferences.getBoolean("reto_${it}_completado", false)
        }
        binding.progressText.text = "Completados: $completedChallenges/$totalDailyChallenges"
        binding.progressBar.progress = (completedChallenges * 100) / totalDailyChallenges

        binding.congratsMessage.apply {
            visibility = if (completedChallenges == totalDailyChallenges) android.view.View.VISIBLE else android.view.View.GONE
            text = "¡Excelente! Has completado todos los retos de hoy 🎉"
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { finish() }
    }
}
