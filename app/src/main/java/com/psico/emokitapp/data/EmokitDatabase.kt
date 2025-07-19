package com.psico.emokitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.psico.emokitapp.data.dao.ActividadCompletadaDao
import com.psico.emokitapp.data.dao.ActividadMeditacionDao
import com.psico.emokitapp.data.dao.DiarioEmocionalDao
import com.psico.emokitapp.data.dao.RetoCompletadoDao
import com.psico.emokitapp.data.dao.UsuarioDao
import com.psico.emokitapp.data.entities.DiarioEmocional
import com.psico.emokitapp.data.entities.RetoCompletado
import com.psico.emokitapp.data.entities.Usuario
import com.psico.emokitapp.data.entities.*
import com.psico.emokitapp.utils.DateConverter

@Database(
    entities = [
        Usuario::class,
        DiarioEmocional::class,
        RetoCompletado::class,
        ActividadMeditacion::class,
        ActividadCompletada::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class EmokitDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun diarioEmocionalDao(): DiarioEmocionalDao
    abstract fun retoCompletadoDao(): RetoCompletadoDao
    abstract fun actividadMeditacionDao(): ActividadMeditacionDao
    abstract fun actividadCompletadaDao(): ActividadCompletadaDao

    companion object {
        @Volatile
        private var INSTANCE: EmokitDatabase? = null

        fun getDatabase(context: Context): EmokitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmokitDatabase::class.java,
                    "emokit_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
