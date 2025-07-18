package com.psico.emokitapp.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.psico.emokitapp.R
import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.ActividadMeditacion
import com.psico.emokitapp.repository.MeditacionRepository
import com.psico.emokitapp.utils.SessionManager
import kotlinx.coroutines.launch

class MeditacionesActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvInstrucciones: TextView
    private lateinit var tvTemporizador: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnIniciar: Button
    private lateinit var btnPausar: Button
    private lateinit var btnNuevaActividad: Button
    private lateinit var tvMensajeCompletado: TextView

    private lateinit var repository: MeditacionRepository
    private lateinit var sessionManager: SessionManager

    private var actividadActual: ActividadMeditacion? = null
    private var temporizador: CountDownTimer? = null
    private var tiempoRestante: Long = 0
    private var tiempoTotal: Long = 0
    private var estaEnPausa = false
    private var actividadCompletada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meditaciones)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
        initializeRepository()
        setupClickListeners()
        cargarNuevaActividad()
    }

    private fun initializeViews() {
        tvTitulo = findViewById(R.id.tvTitulo)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        tvInstrucciones = findViewById(R.id.tvInstrucciones)
        tvTemporizador = findViewById(R.id.tvTemporizador)
        progressBar = findViewById(R.id.progressBar)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausar = findViewById(R.id.btnPausar)
        btnNuevaActividad = findViewById(R.id.btnNuevaActividad)
        tvMensajeCompletado = findViewById(R.id.tvMensajeCompletado)
    }

    private fun initializeRepository() {
        val database = EmokitDatabase.getDatabase(this)
        repository = MeditacionRepository(database)
        sessionManager = SessionManager(this)
    }

    private fun setupClickListeners() {
        btnIniciar.setOnClickListener {
            if (estaEnPausa) {
                reanudarTemporizador()
            } else {
                iniciarTemporizador()
            }
        }
        btnPausar.setOnClickListener {
            pausarTemporizador()
        }
        btnNuevaActividad.setOnClickListener {
            ocultarMensajeCompletado()
            cargarNuevaActividad()
        }
    }

    private fun cargarNuevaActividad() {
        lifecycleScope.launch {
            try {
                actividadActual = repository.getActividadAleatoria()
                actividadActual?.let { actividad ->
                    mostrarActividad(actividad)
                    resetearTemporizador()
                    ocultarMensajeCompletado()
                } ?: run {
                    Toast.makeText(
                        this@MeditacionesActivity,
                        "No se pudo cargar la actividad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MeditacionesActivity,
                    "Error al cargar actividad: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarActividad(actividad: ActividadMeditacion) {
        tvTitulo.text = actividad.titulo
        tvDescripcion.text = actividad.descripcion
        tvInstrucciones.text = actividad.instrucciones

        tiempoTotal = (actividad.duracionMinutos * 60 * 1000).toLong()
        tiempoRestante = tiempoTotal
        actualizarDisplayTemporizador(tiempoRestante)
        progressBar.progress = 0
        animarEntradaCard()
    }

    private fun animarEntradaCard() {
        val cardActividad = findViewById<androidx.cardview.widget.CardView>(R.id.cardActividad)
        val cardTemporizador =
            findViewById<androidx.cardview.widget.CardView>(R.id.cardTemporizador)

        listOf(cardActividad, cardTemporizador).forEach { card ->
            card?.let {
                it.alpha = 0f
                it.scaleX = 0.8f
                it.scaleY = 0.8f

                ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).apply {
                    duration = 300
                    start()
                }
                ObjectAnimator.ofFloat(it, "scaleX", 0.8f, 1f).apply {
                    duration = 300
                    start()
                }
                ObjectAnimator.ofFloat(it, "scaleY", 0.8f, 1f).apply {
                    duration = 300
                    start()
                }
            }
        }
    }

    private fun iniciarTemporizador() {
        actividadCompletada = false
        estaEnPausa = false
        btnIniciar.isEnabled = false
        btnPausar.isEnabled = true
        btnNuevaActividad.isEnabled = false

        temporizador = object : CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                actualizarDisplayTemporizador(millisUntilFinished)
                val progreso =
                    ((tiempoTotal - millisUntilFinished).toFloat() / tiempoTotal.toFloat() * 100).toInt()
                progressBar.progress = progreso
            }
            override fun onFinish() {
                completarActividad()
            }
        }
        temporizador?.start()
    }

    private fun pausarTemporizador() {
        temporizador?.cancel()
        estaEnPausa = true
        btnIniciar.isEnabled = true
        btnIniciar.text = "Reanudar"
        btnPausar.isEnabled = false
    }

    private fun reanudarTemporizador() {
        iniciarTemporizador()
        btnIniciar.text = "Iniciar"
    }

    private fun resetearTemporizador() {
        temporizador?.cancel()
        estaEnPausa = false
        actividadCompletada = false
        btnIniciar.isEnabled = true
        btnIniciar.text = "Iniciar"
        btnPausar.isEnabled = false
        btnNuevaActividad.isEnabled = true

        tiempoRestante = tiempoTotal
        actualizarDisplayTemporizador(tiempoRestante)
        progressBar.progress = 0
    }

    private fun completarActividad() {
        actividadCompletada = true
        btnIniciar.isEnabled = false
        btnPausar.isEnabled = false
        btnNuevaActividad.isEnabled = true

        mostrarMensajeCompletado()
        guardarActividadCompletada()

        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, 100).apply {
            duration = 500
            start()
        }
    }

    private fun mostrarMensajeCompletado() {
        tvMensajeCompletado.visibility = View.VISIBLE
        tvMensajeCompletado.alpha = 0f
        tvMensajeCompletado.scaleX = 0.8f
        tvMensajeCompletado.scaleY = 0.8f

        ObjectAnimator.ofFloat(tvMensajeCompletado, "alpha", 0f, 1f).apply {
            duration = 400
            start()
        }
        ObjectAnimator.ofFloat(tvMensajeCompletado, "scaleX", 0.8f, 1f).apply {
            duration = 400
            start()
        }
        ObjectAnimator.ofFloat(tvMensajeCompletado, "scaleY", 0.8f, 1f).apply {
            duration = 400
            start()
        }
        vibrarDispositivo()
    }

    private fun ocultarMensajeCompletado() {
        if (tvMensajeCompletado.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(tvMensajeCompletado, "alpha", 1f, 0f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(tvMensajeCompletado, "scaleX", 1f, 0.8f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(tvMensajeCompletado, "scaleY", 1f, 0.8f).apply {
                duration = 300
                start()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                tvMensajeCompletado.visibility = View.GONE
            }, 300)
        }
    }

    private fun guardarActividadCompletada() {
        lifecycleScope.launch {
            try {
                val usuario = sessionManager.getUserSession()
                val actividad = actividadActual

                if (usuario != null && actividad != null) {
                    val duracionRealizada = ((tiempoTotal - tiempoRestante) / 1000).toInt()
                    repository.completarActividad(usuario.id, actividad.id, duracionRealizada)
                    mostrarEstadisticas(usuario.id)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MeditacionesActivity,
                    "Error al guardar progreso: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarEstadisticas(usuarioId: Int) {
        lifecycleScope.launch {
            try {
                val actividadesHoy = repository.getActividadesHoy(usuarioId)
                val totalActividades = repository.getHistorialActividades(usuarioId)
                val mensaje =
                    "¡Felicitaciones! Has completado ${actividadesHoy.size} actividades hoy y ${totalActividades.size} en total."
                Toast.makeText(this@MeditacionesActivity, mensaje, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {}
        }
    }

    private fun actualizarDisplayTemporizador(millisUntilFinished: Long) {
        val minutos = (millisUntilFinished / 1000) / 60
        val segundos = (millisUntilFinished / 1000) % 60

        tvTemporizador.text = String.format("%02d:%02d", minutos, segundos)
    }

    private fun vibrarDispositivo() {
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    android.os.VibrationEffect.createOneShot(
                        500,
                        android.os.VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {}
    }
    override fun onDestroy() {
        super.onDestroy()
        temporizador?.cancel()
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
    }
}