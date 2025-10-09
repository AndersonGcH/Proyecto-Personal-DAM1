package com.example.mimonto.adapter

import android.view.LayoutInflater
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mimonto.R
import com.example.mimonto.entity.Transaccion
import kotlin.text.format
import java.text.NumberFormat
import java.util.Locale

class HistorialAdapter (private val listaHistorial: List<Transaccion>) : RecyclerView.Adapter<HistorialAdapter.TransaccionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = listaHistorial[position]
        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US)

        holder.tvCategoria.text = transaccion.tvCategoria
        holder.tvDescripcion.text = transaccion.tvDescripcion

        if (transaccion.tvTipo == "Gasto") {
            holder.tvMonto.text = "- ${formatoMoneda.format(transaccion.tvMonto)}"
            holder.tvMonto.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.tvMonto.text = "+ ${formatoMoneda.format(transaccion.tvMonto)}"
            holder.tvMonto.setTextColor(Color.parseColor("#388E3C"))
        }
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
    }

}