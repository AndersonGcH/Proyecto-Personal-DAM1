package com.example.mimonto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mimonto.entity.Transaccion

data class GastoPorCategoria(
    val categoria: String,
    val total: Double
)
@Dao
interface TransaccionDAO {
    @Insert
    suspend fun agregar(transaccion: Transaccion)
    
    @Update
    suspend fun actualizar(transaccion: Transaccion)

    @Query("SELECT * FROM transacciones ORDER BY id DESC")
    suspend fun obtenerTodas(): List<Transaccion>
    @Delete
    suspend fun eliminar(transaccion: Transaccion)
    @Query("SELECT SUM(monto) FROM transacciones WHERE tipo = :tipo")
    suspend fun obtenerSumaPorTipo(tipo: String): Double?
    @Query("""
    SELECT categoria, SUM(monto) as total
    FROM transacciones
    WHERE tipo = 'Gasto'
    GROUP BY categoria
    HAVING total > 0
""")

    suspend fun obtenerGastosAgrupadosPorCategoria(): List<GastoPorCategoria>
}