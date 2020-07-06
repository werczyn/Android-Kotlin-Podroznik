package com.example.podroznik.dbaccess

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.podroznik.models.database.NoteDatabase

class App: Application() {
    companion object {
        lateinit var db: NoteDatabase
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, NoteDatabase::class.java, "notes.db").build()
        context = applicationContext
    }

    override fun onTerminate() {
        db.close()
        super.onTerminate()
    }

}