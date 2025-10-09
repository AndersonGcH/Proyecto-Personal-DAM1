package com.example.mimonto.entity

import java.util.Date

data class Transaccion (
    val id: String,
    val tvMonto: Double,
    val tvTipo: String,
    val tvCategoria: String,
    val tvDescripcion: String,
    val tvFecha: Date

)

