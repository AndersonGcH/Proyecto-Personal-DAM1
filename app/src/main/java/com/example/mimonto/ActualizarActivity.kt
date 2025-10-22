package com.example.mimonto

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityActualizarBinding
import kotlinx.coroutines.launch


class ActualizarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActualizarBinding
    private lateinit var db: DBHelper
    private var transaccionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)
        transaccionId = intent.getIntExtra("TRANSACCION_ID", -1)

        if (transaccionId == -1) {
            Toast.makeText(this, "Error al cargar la transacción", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosTransaccion()

        binding.btnActualizar.setOnClickListener { actualizarTransaccion() }
        binding.btnEliminar.setOnClickListener { mostrarDialogoDeEliminacion() }
        binding.btnCancelar.setOnClickListener { finish() }
    }

    private fun cargarDatosTransaccion() {
        lifecycleScope.launch {
            val transaccion = db.transaccionDao().obtenerPorId(transaccionId)
            if (transaccion != null) {
                binding.tietMonto.setText(transaccion.monto.toString())
                binding.tietDescripcion.setText(transaccion.descripcion)
                binding.tietCategoria.setText(transaccion.categoria)
                if (transaccion.tipo == "Ingreso") {
                    binding.rbIngreso.isChecked = true
                } else {
                    binding.rbGasto.isChecked = true
                }
            } else {
                Toast.makeText(this@ActualizarActivity, "Transacción no encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarTransaccion() {
        val montoStr = binding.tietMonto.text.toString()
        val descripcion = binding.tietDescripcion.text.toString()
        val categoria = binding.tietCategoria.text.toString()
        val tipoTransaccion = findViewById<RadioButton>(binding.rgTipoTransaccion.checkedRadioButtonId).text.toString()

        if (montoStr.isEmpty() || descripcion.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (descripcion.length > 40) {
            Toast.makeText(this, "La descripción no debe tener más de 40 caracteres", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.tietCategoria.text.toString().trim().length > 20) {
            Toast.makeText(this, "La categoría no debe tener más de 20 caracteres", Toast.LENGTH_SHORT).show()
            return
        }
        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }
        val decimalParts = montoStr.split(".")
        if (decimalParts.size > 1 && decimalParts[1].length > 2) {
            Toast.makeText(this, "El monto puede tener hasta dos decimales", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val transaccionActual = db.transaccionDao().obtenerPorId(transaccionId)
            if (transaccionActual != null) {
                val transaccionActualizada = transaccionActual.copy(
                    monto = monto,
                    descripcion = descripcion,
                    categoria = categoria,
                    tipo = tipoTransaccion
                )
                db.transaccionDao().actualizar(transaccionActualizada)
                Toast.makeText(this@ActualizarActivity, "Transacción actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun mostrarDialogoDeEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transacción")
            .setMessage("¿Deseas eliminar esta transacción? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTransaccion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarTransaccion() {
        lifecycleScope.launch {
            val transaccion = db.transaccionDao().obtenerPorId(transaccionId)
            if (transaccion != null) {
                db.transaccionDao().eliminar(transaccion)
                Toast.makeText(this@ActualizarActivity, "Transacción eliminada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
