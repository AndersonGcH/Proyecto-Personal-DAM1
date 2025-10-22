package com.example.mimonto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mimonto.entity.Transaccion
import com.example.mimonto.entity.Usuario

@Database(entities = [Usuario::class, Transaccion::class], version = 3)
abstract class DBHelper : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDAO
    abstract fun transaccionDao(): TransaccionDAO
    companion object {
        @Volatile
        private var INSTANCE: DBHelper? = null
        fun getDatabase(context: Context): DBHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBHelper::class.java,
                    "MiMontoDB"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}