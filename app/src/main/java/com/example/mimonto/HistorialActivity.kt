package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimonto.adapter.TransaccionAdapter
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityHistorialBinding
import com.example.mimonto.entity.Transaccion
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistorialBinding
    private lateinit var db: DBHelper
    private lateinit var adapter: TransaccionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)

        setupRecyclerView()

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(this@HistorialActivity, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTransacciones()
    }
    private fun setupRecyclerView() {
        adapter = TransaccionAdapter(emptyList()) { transaccion ->
            mostrarDialogoDeEliminacion(transaccion)
        }
        binding.rvHistorial.adapter = adapter
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarTransacciones() {
        lifecycleScope.launch {
            val listaTransacciones = db.transaccionDao().obtenerTodas()
            adapter.actualizarLista(listaTransacciones.reversed())
        }
    }
    private fun mostrarDialogoDeEliminacion(transaccion: Transaccion) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transacción")
            .setMessage("¿Seguro de eliminar '${transaccion.descripcion}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTransaccion(transaccion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarTransaccion(transaccion: Transaccion) {
        lifecycleScope.launch {
            db.transaccionDao().eliminar(transaccion)
            cargarTransacciones()
        }
    }
}