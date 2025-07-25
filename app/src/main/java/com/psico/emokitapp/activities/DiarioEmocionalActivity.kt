package com.psico.emokitapp.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.psico.emokitapp.R
import com.psico.emokitapp.data.EmokitDatabase
import com.psico.emokitapp.data.entities.DiarioEmocional
import com.psico.emokitapp.helpers.ReflexionCardHelper
import com.psico.emokitapp.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

class DiarioEmocionalActivity : AppCompatActivity() {

    private lateinit var database: EmokitDatabase
    private lateinit var containerReflexiones: LinearLayout
    private lateinit var layoutReflexiones: LinearLayout
    private lateinit var etDescripcion: EditText
    private lateinit var cardHelper: ReflexionCardHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var userEmail: String

    private var emocionSeleccionada: String = ""
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private lateinit var emotionButtons: List<ImageView>

    private enum class Emocion(val nombre: String, val drawable: Int) {
        FELIZ("feliz", R.drawable.ic_happy),
        TRISTE("triste", R.drawable.ic_sad),
        ENOJADO("enojado", R.drawable.ic_angry),
        SORPRENDIDO("sorprendido", R.drawable.ic_surprised),
        ANSIOSO("ansioso", R.drawable.ic_sadx2),
        NEUTRAL("neutral", R.drawable.ic_serious);

        companion object {
            fun fromNombre(nombre: String): Emocion? = values().find { it.nombre == nombre }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_diario_emocional)

        sessionManager = SessionManager(this)
        val user = sessionManager.getUserSession()
        if (user == null) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        userEmail = user.correo

        initializeViews()
        setupInitialState()
        setupListeners()
    }

    private fun initializeViews() {
        database = EmokitDatabase.getDatabase(this)
        containerReflexiones = findViewById(R.id.containerReflexiones)
        layoutReflexiones = findViewById(R.id.layoutReflexiones)
        etDescripcion = findViewById(R.id.etDescripcion)
        cardHelper = ReflexionCardHelper(this)

        emotionButtons = listOf(
            findViewById(R.id.btnFeliz),
            findViewById(R.id.btnTriste),
            findViewById(R.id.btnEnojado),
            findViewById(R.id.btnSorprendido),
            findViewById(R.id.btnAnsioso),
            findViewById(R.id.btnNeutral)
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupInitialState() {
        val ahora = Date()
        findViewById<TextView>(R.id.tvFecha).text = "${dateFormat.format(ahora)} - ${timeFormat.format(ahora)}"

        setupEmotionButtons()
        emotionButtons.forEach { button -> button.alpha = 0.7f }
        limpiarCampos()

        lifecycleScope.launch {
            mostrarEntradasDelDia()
            cargarReflexionesAnteriores()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupListeners() {
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { validarYGuardar() }
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupEmotionButtons() {
        val emociones = Emocion.values()
        emotionButtons.forEachIndexed { index, imageView ->
            val emocion = emociones[index]
            imageView.apply {
                setImageResource(emocion.drawable)
                setOnClickListener { seleccionarEmocion(emocion.nombre, this) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun validarYGuardar() {
        val descripcion = etDescripcion.text.toString().trim()
        when {
            emocionSeleccionada.isEmpty() -> showToast("Selecciona una emoción")
            descripcion.isEmpty() -> showToast("Escribe una descripción")
            else -> guardarEntrada(descripcion)
        }
    }

    private fun seleccionarEmocion(emocion: String, imageView: ImageView) {
        emocionSeleccionada = emocion
        resetButtonEffects()

        imageView.animate().scaleX(1.15f).scaleY(1.15f).alpha(1.0f).translationY(-4f).setDuration(250).start()
        imageView.elevation = 8f

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun resetButtonEffects() {
        emotionButtons.forEach { button ->
            button.animate().scaleX(1.0f).scaleY(1.0f).alpha(0.7f).translationY(0f).setDuration(250).start()
            button.elevation = 0f
        }
    }

    private fun limpiarCampos() {
        emocionSeleccionada = ""
        etDescripcion.setText("")
        resetButtonEffects()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun cargarReflexionesAnteriores() {
        lifecycleScope.launch {
            val reflexiones = database.diarioEmocionalDao().getUltimasReflexiones(userEmail, 5)
            layoutReflexiones.visibility = if (reflexiones.isNotEmpty()) {
                mostrarReflexiones(reflexiones)
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun mostrarReflexiones(reflexiones: List<DiarioEmocional>) {
        containerReflexiones.removeAllViews()
        reflexiones.forEach { reflexion ->
            containerReflexiones.addView(cardHelper.crearCardReflexion(reflexion))
        }
    }

    private fun mostrarEntradasDelDia() {
        lifecycleScope.launch {
            val hoy = getStartOfDay()
            val entradasHoy = database.diarioEmocionalDao().contarEntradasPorFecha(userEmail, hoy)
            if (entradasHoy > 0) {
                showToast("Ya tienes $entradasHoy reflexión(es) hoy")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun guardarEntrada(descripcion: String) {
        lifecycleScope.launch {
            val nuevaEntrada = DiarioEmocional(
                usuarioCorreo = userEmail,
                emocion = emocionSeleccionada,
                descripcion = descripcion,
                fecha = getStartOfDay(),
                timestamp = Date()
            )
            database.diarioEmocionalDao().insertEntrada(nuevaEntrada)
            showToast("Reflexión guardada exitosamente")
            cargarReflexionesAnteriores()
            limpiarCampos()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
