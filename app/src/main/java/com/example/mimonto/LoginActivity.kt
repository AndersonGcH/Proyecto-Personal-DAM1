package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.data.SessionManager
import com.example.mimonto.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper.getDatabase(this)
        sessionManager = SessionManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnInicio.setOnClickListener {
            iniciarSesion()
        }

        binding.btnRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion() {
        val correo = binding.tietCorreo.text.toString().trim()
        val clave = binding.tietClave.text.toString().trim()

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val usuario = db.usuarioDao().login(correo, clave)

            runOnUiThread {
                if (usuario == null) {
                    Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                } else {
                    sessionManager.saveAuthToken(usuario.id)
                    Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombres}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, PrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}