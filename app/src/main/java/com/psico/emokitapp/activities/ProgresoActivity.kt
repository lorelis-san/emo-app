package com.psico.emokitapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.psico.emokitapp.adapters.ProgresoAdapter
import com.psico.emokitapp.databinding.ActivityProgresoBinding
import com.psico.emokitapp.utils.SessionManager
import com.psico.emokitapp.viewmodel.ProgresoViewModel

class ProgresoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgresoBinding
    private lateinit var progresoAdapter: ProgresoAdapter

    private val progresoViewModel: ProgresoViewModel by viewModels()

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

        progresoViewModel.getRetosHoy(usuario.correo).observe(this) { retosHoy ->
            val count = retosHoy.size
            binding.tvResumenSemana.text = "🏆 Retos completados hoy: $count"
            progresoAdapter.submitList(retosHoy)
        }

        progresoViewModel.getProgresoSemana(usuario.correo).observe(this) { progresoSemana ->
            val resumen = StringBuilder("📈 Progreso de la semana:\n")
            progresoSemana.forEach { (dia, cantidad) ->
                val icon = if (cantidad > 0) "🏆$cantidad" else "0"
                resumen.append("$dia: $icon\n")
            }
            binding.tvResumenSemana.append("\n$resumen")
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
}
