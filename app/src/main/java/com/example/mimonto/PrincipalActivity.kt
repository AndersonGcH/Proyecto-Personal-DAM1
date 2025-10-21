package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityPrincipalBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat

class PrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        actualizarUI()
    }
    private fun setupListeners() {
        binding.btnAgregarTransaccion.setOnClickListener {
            val intent = Intent(this, AgregarActivity::class.java)
            startActivity(intent)
        }

        binding.btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        binding.btnEstadistica.setOnClickListener {
            val fragmentContainer = binding.fragmentContainer
            val currentFragment = supportFragmentManager.findFragmentById(fragmentContainer.id)

            if (currentFragment is EstadisticaFragment) {
                supportFragmentManager.beginTransaction()
                    .remove(currentFragment)
                    .commit()
            } else {
                val estadisticasFragment = EstadisticaFragment()
                supportFragmentManager.beginTransaction()
                    .replace(fragmentContainer.id, estadisticasFragment)
                    .commit()
            }
        }
    }
    private fun actualizarUI() {
        lifecycleScope.launch {
            val totalIngresos = db.transaccionDao().obtenerSumaPorTipo("Ingreso") ?: 0.0
            val totalGastos = db.transaccionDao().obtenerSumaPorTipo("Gasto") ?: 0.0
            val balance = totalIngresos - totalGastos

            val format = NumberFormat.getCurrencyInstance(java.util.Locale("es", "PE"))

            binding.tvTotalIngresos.text = format.format(totalIngresos)
            binding.tvTotalGastos.text = format.format(totalGastos)
            binding.tvBalance.text = format.format(balance)
        }
    }
}