package com.example.podroznik.dbaccess

import android.content.Context
import androidx.room.Room
import com.example.podroznik.models.database.NoteDatabase

class DbAccess private constructor(applicationContext: Context) {
    companion object {
        private var dbAccess: DbAccess? = null
        fun getInstance(applicationContext: Context): DbAccess {
            return dbAccess
                ?: DbAccess(applicationContext)
                    .also { dbAccess = it }
        }
    }

    val db by lazy {
        Room.databaseBuilder(applicationContext, NoteDatabase::class.java, "notes.db")
            .build()
    }
}