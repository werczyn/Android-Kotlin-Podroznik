package com.example.podroznik.models

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var content: String,
    var photo: String?,
    var date: String?,
    var latitude : Double?,
    var longitude : Double?
):Serializable