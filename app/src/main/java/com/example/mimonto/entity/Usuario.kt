package com.example.mimonto.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nombres: String,
    var apellidos: String,
    var correo: String,
    var clave: String
)