package com.psico.emokitapp

import android.app.Application
import androidx.room.Room
import com.psico.emokitapp.data.EmokitDatabase

class EmokitApp : Application() {

    companion object {
        lateinit var database: EmokitDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            EmokitDatabase::class.java,
            "emokit_database"
        ).build()
    }
}
