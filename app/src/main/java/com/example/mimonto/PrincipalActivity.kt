package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.data.SessionManager
import com.example.mimonto.databinding.ActivityPrincipalBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class PrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var db: DBHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)
        sessionManager = SessionManager(this)
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
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                val totalIngresos = db.transaccionDao().obtenerSumaPorTipo("Ingreso", userId) ?: 0.0
                val totalGastos = db.transaccionDao().obtenerSumaPorTipo("Gasto", userId) ?: 0.0
                val balance = totalIngresos - totalGastos

                val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

                binding.tvTotalIngresos.text = format.format(totalIngresos)
                binding.tvTotalGastos.text = format.format(totalGastos)
                binding.tvBalance.text = format.format(balance)
            }
        }
    }
}