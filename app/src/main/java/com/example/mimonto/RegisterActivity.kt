package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityRegisterBinding
import com.example.mimonto.entity.Usuario
import kotlinx.coroutines.launch
class RegisterActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)

        setupListeners()
    }
    private fun setupListeners() {
        binding.btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registrarUsuario() {
        val nombreText = binding.tietNombres.text.toString().trim()
        val apellidoText = binding.tietApellidos.text.toString().trim()
        val correoText = binding.tietCorreo.text.toString().trim()
        val claveText = binding.tietClave.text.toString().trim()

        if (nombreText.isEmpty() || apellidoText.isEmpty() || correoText.isEmpty() || claveText.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correoText).matches()) {
            Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.tietCorreo.error = null
        }

        if (claveText.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (!claveText.any { it.isDigit() }) {
            Toast.makeText(this, "La contraseña debe tener al menos un número", Toast.LENGTH_SHORT).show()
            return
        }

        if (nombreText.length < 3 || apellidoText.length < 3) {
            Toast.makeText(this, "Nombres y apellidos deben tener al menos 3 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val usuario = Usuario(
                nombres = nombreText,
                apellidos = apellidoText,
                correo = correoText,
                clave = claveText
            )
            db.usuarioDao().registrarUsuario(usuario)

            runOnUiThread {
                Toast.makeText(this@RegisterActivity, "Usuario registrado", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}