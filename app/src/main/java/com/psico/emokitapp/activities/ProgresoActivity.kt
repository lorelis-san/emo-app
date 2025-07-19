package com.psico.emokitapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.psico.emokitapp.adapters.ProgresoAdapter
import com.psico.emokitapp.databinding.ActivityProgresoBinding
import com.psico.emokitapp.utils.SessionManager
import com.psico.emokitapp.viewmodel.ProgresoViewModel

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.psico.emokitapp.R
import com.psico.emokitapp.data.EmokitDatabase
import kotlinx.coroutines.launch
import java.util.Calendar

class ProgresoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgresoBinding
    private lateinit var progresoAdapter: ProgresoAdapter

    private val progresoViewModel: ProgresoViewModel by viewModels()

    private lateinit var barChartEmociones: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgresoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        val usuario = sessionManager.getUserSession()

        if (usuario == null) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()

        barChartEmociones = findViewById(R.id.barChart)
        cargarGraficoEmociones(usuario.correo)

        progresoViewModel.getRetosHoy(usuario.correo).observe(this) { retosHoy ->
            val count = retosHoy.size
            binding.tvRetosCompletados.text = "🏆 Retos completados hoy: $count"
            progresoAdapter.submitList(retosHoy)
        }

        progresoViewModel.getProgresoSemana(usuario.correo).observe(this) { progresoSemana ->
            progresoSemana.forEach { (dia, cantidad) ->
                when (dia) {
                    "Mon" -> binding.tvLunes.text = cantidad.toString()
                    "Tue" -> binding.tvMartes.text = cantidad.toString()
                    "Wed" -> binding.tvMiercoles.text = cantidad.toString()
                    "Thu" -> binding.tvJueves.text = cantidad.toString()
                    "Fri" -> binding.tvViernes.text = cantidad.toString()
                    "Sat" -> binding.tvSabado.text = cantidad.toString()
                    "Sun" -> binding.tvDomingo.text = cantidad.toString()
                }
            }

        }

        binding.backButton.setOnClickListener { finish() }


    }

    private fun setupRecyclerView() {
        progresoAdapter = ProgresoAdapter()
        binding.recyclerViewProgreso.apply {
            layoutManager = LinearLayoutManager(this@ProgresoActivity)
            adapter = progresoAdapter
        }
    }

    private fun cargarGraficoEmociones(correo: String) {
        val db = EmokitDatabase.getDatabase(this)
        val diarioEmocionalDao = db.diarioEmocionalDao()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        lifecycleScope.launch {
            val emotionCountMap = mutableMapOf<String, Int>()

            for (i in 0..6) {
                val startOfDay = calendar.clone() as Calendar
                startOfDay.set(Calendar.HOUR_OF_DAY, 0)
                startOfDay.set(Calendar.MINUTE, 0)
                startOfDay.set(Calendar.SECOND, 0)
                startOfDay.set(Calendar.MILLISECOND, 0)

                val endOfDay = calendar.clone() as Calendar
                endOfDay.set(Calendar.HOUR_OF_DAY, 23)
                endOfDay.set(Calendar.MINUTE, 59)
                endOfDay.set(Calendar.SECOND, 59)
                endOfDay.set(Calendar.MILLISECOND, 999)

                val entradas = diarioEmocionalDao.getEntradasPorUsuarioYRango(
                    correo,
                    startOfDay.time,
                    endOfDay.time
                )

                entradas.forEach { entry ->
                    val emocion = entry.emocion.lowercase().trim()
                    emotionCountMap[emocion] = (emotionCountMap[emocion] ?: 0) + 1
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            if (emotionCountMap.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(this@ProgresoActivity, "Sin datos para mostrar en el gráfico.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            emotionCountMap.entries.forEachIndexed { index, entry ->
                entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
                labels.add(entry.key)
            }

            val dataSet = BarDataSet(entries, "Emociones de la Semana").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 14f
            }

            val data = BarData(dataSet).apply {
                barWidth = 0.8f
            }

            runOnUiThread {
                barChartEmociones.apply {
                    this.data = data
                    setFitBars(true)

                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        granularity = 1f
                        isGranularityEnabled = true
                        setDrawGridLines(false)
                        position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                        textSize = 12f
                    }

                    axisLeft.axisMinimum = 0f
                    axisRight.isEnabled = false
                    description.isEnabled = false
                    legend.isEnabled = true

                    animateY(1000)
                    invalidate()
                }
            }
        }
    }

}
