package com.psico.emokitapp.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.psico.emokitapp.R
import com.psico.emokitapp.data.entities.Reto

class RetosAdapter(
    private val retos: MutableList<Reto>,
    private val onRetoClick: (Reto) -> Unit
) : RecyclerView.Adapter<RetosAdapter.RetoViewHolder>() {

    class RetoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.retoCard)
        val tituloTextView: TextView = itemView.findViewById(R.id.retoTitulo)
        val descripcionTextView: TextView = itemView.findViewById(R.id.retoDescripcion)
        val recompensaTextView: TextView = itemView.findViewById(R.id.retoRecompensa)
        val checkBox: CheckBox = itemView.findViewById(R.id.retoCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reto, parent, false)
        return RetoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        val reto = retos[position]

        holder.tituloTextView.text = reto.titulo
        holder.descripcionTextView.text = reto.descripcion
        holder.recompensaTextView.text = reto.recompensa
        holder.checkBox.isChecked = reto.completado

        if (reto.completado) {
            holder.cardView.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.success_light)
            )
            holder.tituloTextView.setTextColor(
                holder.itemView.context.getColor(R.color.success_dark)
            )
            holder.descripcionTextView.setTextColor(
                holder.itemView.context.getColor(R.color.success_dark)
            )
        } else {
            holder.cardView.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.white)
            )
            holder.tituloTextView.setTextColor(
                holder.itemView.context.getColor(R.color.primary_dark)
            )
            holder.descripcionTextView.setTextColor(
                holder.itemView.context.getColor(R.color.gray_dark)
            )
        }

        holder.cardView.setOnClickListener {
            onRetoClick(reto)
        }

        holder.checkBox.setOnClickListener {
            onRetoClick(reto)
        }
    }

    override fun getItemCount(): Int = retos.size

    fun updateReto(updatedReto: Reto) {
        val index = retos.indexOfFirst { it.id == updatedReto.id }
        if (index != -1) {
            retos[index] = updatedReto
            notifyItemChanged(index)
        }
    }
}