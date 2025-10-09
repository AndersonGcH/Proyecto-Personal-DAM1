package com.example.mimonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mimonto.entity.Usuario
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietClave : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilClave : TextInputLayout
    private lateinit var btnAcceso : Button

    private val listaUsuarios = mutableListOf(
        Usuario(1, "Anderson Gioel", "Cutipa Higinio", "agcutipa@cibertec.edu.pe", "1234"),
        Usuario(2, "Nombres", "Apellidos", "prueba@cibertec.edu.pe", "1234")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)
        btnAcceso = findViewById(R.id.btnInicio)

        btnAcceso.setOnClickListener {
            validarCampos()
        }
    }

    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error : Boolean = false
        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            error = true
        } else {
            tilCorreo.error = ""
        }
        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = ""
        }

        if (error) {
            return
        } else {
            var usuarioEncontrado : Usuario?= null
            for (u in listaUsuarios) {
                if (u.correo == correo + "@cibertec.edu.pe" && u.clave == clave) {
                    usuarioEncontrado = u
                    break
                }
            }

            if (usuarioEncontrado != null) {
                Toast.makeText(this, "Bienvenido ${usuarioEncontrado.nombres}", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, PrincipalActivity::class.java))
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                mostrarDialogoError()
            }
        }
    }

    private fun mostrarDialogoError() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error de Acceso")
            .setMessage("El correo electrónico o la contraseña que ingresaste no son correctos. Por favor, verifica tus datos e inténtalo de nuevo.")
            .setPositiveButton("Entendido") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}