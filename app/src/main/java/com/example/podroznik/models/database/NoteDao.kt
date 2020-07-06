package com.example.podroznik.models.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.podroznik.models.Note

@Dao
interface NoteDao {

    @Insert
    fun insert(note : Note)

    @Query("SELECT * FROM Note")
    fun selectAll(): List<Note>

    @Query("SELECT * FROM NOTE WHERE photo=:photo")
    fun selectByPhoto(photo : String):Note

}