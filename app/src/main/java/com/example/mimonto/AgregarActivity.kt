package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityAgregarBinding
import com.example.mimonto.entity.Transaccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
class AgregarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarBinding
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)

        binding.btnGuardarTransaccion.setOnClickListener {
            guardarTransaccion()
        }

        binding.btnPrincipal.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun guardarTransaccion() {
        val montoStr = binding.tietMonto.text.toString().trim()
        val descripcion = binding.tietDescripcion.text.toString().trim()
        val categoria = binding.tietCategoria.text.toString().trim()

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

        val tipo = if (binding.rgTipoTransaccion.checkedRadioButtonId == R.id.rbIngreso) "Ingreso" else "Gasto"
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

        val nuevaTransaccion = Transaccion(
            monto = monto,
            descripcion = descripcion,
            categoria = categoria,
            tipo = tipo,
            fecha = fechaActual
        )

        lifecycleScope.launch (Dispatchers.IO) {
            db.transaccionDao().agregar(nuevaTransaccion)
            runOnUiThread {
                Toast.makeText(this@AgregarActivity, "Transacción Guardada", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@AgregarActivity, PrincipalActivity::class.java)
                    startActivity(intent)
                    finish()
            }
        }
    }
}