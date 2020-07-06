package com.example.podroznik.viewmodels

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podroznik.dbaccess.App
import com.example.podroznik.views.MyImageView
import com.example.podroznik.models.Note
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.concurrent.thread

class NotesVM : ViewModel() {
    private val db by lazy { App.db }
    val goNext = MutableLiveData(false)
    var date = MutableLiveData<String>()
    var location = MutableLiveData<Location>()
    var photo = MutableLiveData<String>()
    var content = MutableLiveData("")
    var note = Note(0, "", "", "", 0.0,0.0)

    fun addNote() {
        note =  Note(0, content.value.toString(), photo.value, date.value, location.value?.latitude, location.value?.longitude)
        if (!content.value.isNullOrBlank()) {
            thread {
                db.getNoteDao().insert(note)
            }
            goNext.value = true
        }
    }

    fun setNote(note : Serializable){
        this.note = note as Note
        content.postValue(note.content)
        photo.postValue(note.photo)
        date.postValue(note.date)

        val loc = Location("")
        val lat = note.latitude
        val lon =  note.longitude
        if (lat != null && lon != null){
            loc.latitude = lat
            loc.longitude = lon
        }
        location.postValue(loc)
    }

    fun processLocation(
        geo: Geocoder,
        noteImageView: MyImageView
    ) {

        val address = geo.getFromLocation(location.value!!.latitude, location.value!!.longitude,1)
        if (address.isNotEmpty()){
            val country =address[0].countryName
            val city =address[0].locality
            noteImageView.country = country
            noteImageView.city = city
            noteImageView.invalidate()
        }else{
            noteImageView.country = "Kraj"
            noteImageView.city = "Miasto"
        }
    }

    fun setPhoto(photoAbsolutePath : String){
        photo.postValue(photoAbsolutePath)
        date.postValue( LocalDateTime.now().toLocalDate().toString())
    }

    fun getNoteByPhoto(photo : String) {
        thread {
            setNote(db.getNoteDao().selectByPhoto(photo))
        }
    }
}