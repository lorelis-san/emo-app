package com.psico.emokitapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.psico.emokitapp.data.dao.UsuarioDao
import com.psico.emokitapp.data.entities.Usuario

@Database(entities = [Usuario::class], version = 1)
abstract class EmokitDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}
