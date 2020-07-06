package com.example.podroznik.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podroznik.dbaccess.App
import com.example.podroznik.models.Note
import java.io.Serializable
import kotlin.concurrent.thread

class MainVM : ViewModel() {
    private val db by lazy { App.db }

    private val goNext = MutableLiveData<Boolean>(false)
    val notes = MutableLiveData<MutableList<Note>>()

    init {
        if (notes.value.isNullOrEmpty()){
            onLoad()
        }
    }

    fun getGoNext() : LiveData<Boolean> {
        return goNext
    }

    fun addNoteClicked(){
        goNext.value = true
        goNext.value = false
    }

    fun insertNote(note : Serializable){
        notes.value?.add(note as Note)
        notes.postValue(notes.value)
    }

    private fun onLoad(){
        thread {
            val notedb = db.getNoteDao().selectAll().toMutableList()
            notes.postValue(notedb)
        }
    }
}