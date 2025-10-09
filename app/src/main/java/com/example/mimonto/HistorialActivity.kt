package com.example.mimonto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mimonto.adapter.HistorialAdapter
import com.example.mimonto.entity.Transaccion
import java.util.Date

class HistorialActivity : AppCompatActivity() {
    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private var listaDeTransacciones = mutableListOf<Transaccion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)
        cargarDatosDeEjemplo()

        rvHistorial = findViewById(R.id.rvHistorial)

        rvHistorial.layoutManager = LinearLayoutManager(this)

        historialAdapter = HistorialAdapter(listaDeTransacciones)
        rvHistorial.adapter = historialAdapter
    }
    private fun cargarDatosDeEjemplo() {
        listaDeTransacciones.add(
            Transaccion(
                id = "1",
                tvMonto = 50.75,
                tvTipo = "Gasto",
                tvCategoria = "Alimentación",
                tvDescripcion = "Almuerzo en restaurante",
                tvFecha = Date()
            )
        )
        listaDeTransacciones.add(
            Transaccion(
                id = "2",
                tvMonto = 1130.00,
                tvTipo = "Ingreso",
                tvCategoria = "Salario",
                tvDescripcion = "Salario trabajo",
                tvFecha = Date()
            )
        )
    }
}