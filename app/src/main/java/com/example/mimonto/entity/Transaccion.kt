package com.example.mimonto.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var monto: Double,
    var tipo: String,
    var categoria: String,
    var descripcion: String,
    var fecha: String
)

