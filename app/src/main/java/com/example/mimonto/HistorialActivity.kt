package com.example.mimonto

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimonto.adapter.TransaccionAdapter
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.ActivityHistorialBinding
import com.example.mimonto.entity.Transaccion
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class HistorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistorialBinding
    private lateinit var db: DBHelper
    private lateinit var adapter: TransaccionAdapter
    private var listaTransaccionesCompleta: List<Transaccion> = emptyList()

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }

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

        binding.btnDescargarPdf.setOnClickListener {
            solicitarPermisosYGenerarPdf()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTransacciones()
    }

    private fun setupRecyclerView() {
        adapter = TransaccionAdapter(emptyList()) { transaccion ->
            val intent = Intent(this, ActualizarActivity::class.java)
            intent.putExtra("TRANSACCION_ID", transaccion.id)
            startActivity(intent)
        }
        binding.rvHistorial.adapter = adapter
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarTransacciones() {
        lifecycleScope.launch {
            listaTransaccionesCompleta = db.transaccionDao().obtenerTodas()
            adapter.actualizarLista(listaTransaccionesCompleta.reversed())
        }
    }

    private fun solicitarPermisosYGenerarPdf() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
        } else {
            generarPdf()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generarPdf()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generarPdf() {
        val transacciones = listaTransaccionesCompleta.reversed()
        if (transacciones.isEmpty()) {
            Toast.makeText(this, "No hay transacciones para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()
        val title = Paint()

        title.textSize = 20f
        title.isFakeBoldText = true
        canvas.drawText("Historial de Transacciones", 40f, 50f, title)
        paint.textSize = 12f
        paint.isFakeBoldText = true
        var y = 80f
        canvas.drawText("Fecha", 40f, y, paint)
        canvas.drawText("Descripción", 180f, y, paint)
        canvas.drawText("Categoría", 360f, y, paint)
        canvas.drawText("Monto", 490f, y, paint)
        y += 10
        canvas.drawLine(40f, y, 555f, y, paint)
        y += 20
        paint.isFakeBoldText = false

        for (transaccion in transacciones) {
            if (y > 800) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
            }
            val montoFormateado = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "PE")).format(transaccion.monto)
            val signo = if (transaccion.tipo == "Ingreso") "+" else "-"
            canvas.drawText(transaccion.fecha, 40f, y, paint)
            canvas.drawText(transaccion.descripcion, 180f, y, paint)
            canvas.drawText(transaccion.categoria, 360f, y, paint)
            canvas.drawText("$signo$montoFormateado", 490f, y, paint)
            y += 25f
        }

        document.finishPage(page)

        val fileName = "Historial_Transacciones_${System.currentTimeMillis()}.pdf"
        var pdfUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                pdfUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                pdfUri?.let {
                    resolver.openOutputStream(it).use { outputStream ->
                        document.writeTo(outputStream)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    document.writeTo(outputStream)
                }
                pdfUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
            }
            document.close()

            if (pdfUri != null) {
                Toast.makeText(this, "PDF guardado", Toast.LENGTH_LONG).show()
                abrirPdf(pdfUri!!)
            } else {
                Toast.makeText(this, "Error al obtener URI del PDF", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar o abrir el PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun abrirPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No se encontró una aplicación para abrir PDF", Toast.LENGTH_SHORT).show()
        }
    }
}