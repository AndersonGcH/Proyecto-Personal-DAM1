package com.example.mimonto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mimonto.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mimonto.entity.Transaccion
class TransaccionAdapter(
    private var transacciones: List<Transaccion>,
    private val onTransaccionLongClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    inner class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewTipoIndicador: View = itemView.findViewById(R.id.viewTipoIndicador)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val categoriaTextView: TextView = itemView.findViewById(R.id.tvCategoria)
        private val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        private val montoTextView: TextView = itemView.findViewById(R.id.tvMonto)
        fun bind(transaccion: Transaccion) {
            descripcionTextView.text = transaccion.descripcion
            categoriaTextView.text = transaccion.categoria
            fechaTextView.text = transaccion.fecha

            val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "PE"))
            val montoFormateado = format.format(transaccion.monto)

            if (transaccion.tipo == "Ingreso") {
                montoTextView.text = "+ ${montoFormateado}"
                montoTextView.setTextColor(android.graphics.Color.parseColor("#388E3C"))
                viewTipoIndicador.setBackgroundColor(android.graphics.Color.parseColor("#388E3C"))
            } else {
                montoTextView.text = "- ${montoFormateado}"
                montoTextView.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                viewTipoIndicador.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F"))
            }

            itemView.setOnLongClickListener {
                onTransaccionLongClick(transaccion)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return TransaccionViewHolder(view)
    }
    override fun getItemCount(): Int = transacciones.size

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        holder.bind(transacciones[position])
    }
    fun actualizarLista(nuevaLista: List<Transaccion>) {
        transacciones = nuevaLista
        notifyDataSetChanged()
    }
}