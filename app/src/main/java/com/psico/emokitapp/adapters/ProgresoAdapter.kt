package com.psico.emokitapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psico.emokitapp.data.entities.RetoCompletado
import com.psico.emokitapp.databinding.ItemProgresoBinding

class ProgresoAdapter : RecyclerView.Adapter<ProgresoAdapter.ProgresoViewHolder>() {

    private val listaRetos = mutableListOf<RetoCompletado>()

    inner class ProgresoViewHolder(private val binding: ItemProgresoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reto: RetoCompletado) {
            binding.tvTituloReto.text = reto.retoTitulo
            binding.tvDescripcionReto.text = "📅 ${reto.fecha}"
//            binding.tvRecompensaReto.text = "🏆 ¡Completado!"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgresoViewHolder {
        val binding = ItemProgresoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProgresoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgresoViewHolder, position: Int) {
        holder.bind(listaRetos[position])
    }

    override fun getItemCount(): Int = listaRetos.size

    fun submitList(retos: List<RetoCompletado>) {
        listaRetos.clear()
        listaRetos.addAll(retos)
        notifyDataSetChanged()
    }
}
