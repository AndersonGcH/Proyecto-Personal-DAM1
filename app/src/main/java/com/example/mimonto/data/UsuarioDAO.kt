package com.example.mimonto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mimonto.entity.Usuario

@Dao
interface UsuarioDAO {
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

}