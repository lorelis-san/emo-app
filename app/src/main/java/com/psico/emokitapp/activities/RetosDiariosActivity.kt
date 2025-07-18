package com.psico.emokitapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.psico.emokitapp.adapters.RetosAdapter
import com.psico.emokitapp.data.entities.Reto
import com.psico.emokitapp.data.entities.RetoCompletado
import com.psico.emokitapp.databinding.ActivityRetosDiariosBinding
import com.psico.emokitapp.viewmodel.RetoCompletadoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class RetosDiariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRetosDiariosBinding
    private lateinit var retosAdapter: RetosAdapter
    private lateinit var userEmail: String

    private var completedChallenges = 0
    private val totalDailyChallenges = 4

    private val retoCompletadoViewModel: RetoCompletadoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetosDiariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("user_email") ?: run {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupRetosRecyclerView()
        setupClickListeners()
    }

    private fun setupUI() {
        updateProgress()
    }

    private fun setupRetosRecyclerView() {
        val dailyRetos = generateDailyRetos().toMutableList()

        retosAdapter = RetosAdapter(dailyRetos) { reto ->
            toggleRetoCompletion(reto)
        }

        binding.retosRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.retosRecyclerView.adapter = retosAdapter
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

    private fun toggleRetoCompletion(reto: Reto) {
        if (reto.completado) {
            Toast.makeText(this, "Este reto ya fue completado", Toast.LENGTH_SHORT).show()
            return
        }

        retosAdapter.updateReto(reto.copy(completado = true))
        completedChallenges++

        val retoCompletado = RetoCompletado(
            usuarioCorreo = userEmail,
            retoTitulo = reto.titulo,
            fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        lifecycleScope.launch {
            retoCompletadoViewModel.insertarRetoCompletado(retoCompletado)
            showRewardMessage(reto.recompensa) // muestra recompensa tras guardar
            updateProgress() // actualiza progreso tras guardar
        }

    }

    private fun showRewardMessage(reward: String) {
        Toast.makeText(this, "¡Felicidades! Has ganado: $reward", Toast.LENGTH_SHORT).show()
    }

    private fun updateProgress() {
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
