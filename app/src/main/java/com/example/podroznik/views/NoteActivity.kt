package com.example.podroznik.views

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.blue
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.podroznik.R
import com.example.podroznik.databinding.ActivityNoteBinding
import com.example.podroznik.dbaccess.App.Companion.context
import com.example.podroznik.recivers.MyReciver
import com.example.podroznik.viewmodels.NotesVM
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_note.*
import java.io.File
import java.time.LocalDateTime
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var photoFile : File
    private val notesVM by lazy { ViewModelProvider(this).get(NotesVM::class.java) }
    private var radius = 1000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBindingUtil = DataBindingUtil.setContentView<ActivityNoteBinding>(this, R.layout.activity_note)

        dataBindingUtil.let {
            it.notesVM = notesVM
            it.lifecycleOwner = this
        }

        notesVM.goNext.observe({this.lifecycle}){
            if (it) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra("note", notesVM.note)
                })
                registerGeofance(notesVM.location.value!!.latitude, notesVM.location.value!!.longitude, radius)
                finish()
            }
        }

        notesVM.photo.observe({this.lifecycle}){
            noteImageView.setImageBitmap(BitmapFactory.decodeFile(it))
        }

        notesVM.date.observe({this.lifecycle}){
            noteImageView.date = it
        }

        notesVM.location.observe({this.lifecycle}){
            val geo = Geocoder(this,Locale.getDefault())
            notesVM.processLocation(geo, noteImageView)
        }

        when {
            intent.hasExtra("note") -> {
                noteEditText.isEnabled = false
                notesAddButton.visibility = View.GONE
                val note = intent.getSerializableExtra("note")
                notesVM.setNote(note)
            }
            intent.hasExtra("photo") && intent.getStringExtra("photo") != null -> {
                noteEditText.isEnabled = false
                notesAddButton.visibility = View.GONE
                val photo = intent.getStringExtra("photo")
                notesVM.getNoteByPhoto(photo)
            }
            else -> {
                photo()
                checkLocation()
            }
        }

        ustawienia()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            notesVM.setPhoto(photoFile.absolutePath)
        }
    }

    private fun photo() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
            photoFile = this.filesDir.resolve(LocalDateTime.now().toString()+".jpg").also { it.createNewFile() }
            val uri = FileProvider.getUriForFile(
                this,
                resources.getString(R.string.file_provider),
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(takePictureIntent, 1)

        }
    }

    private fun ustawienia(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        radius = prefs.getInt("radius",R.integer.radius_default).toFloat()
        val textSize = prefs.getInt("textSize",R.integer.text_size_default)
        val color = prefs.getString("colorList","#3F51B5")
        noteImageView.paint.color = Color.parseColor(color)
        noteImageView.paint.textSize = textSize.toFloat()
    }

    private fun checkLocation(){
        checkPermissions()
        val locationManager = LocationServices.getFusedLocationProviderClient(this)
        val location = locationManager.lastLocation.addOnSuccessListener {
            notesVM.location.postValue(it)
        }
    }

    private fun registerGeofance(latitude : Double, longitude : Double, radius : Float){
        val geofances = LocationServices.getGeofencingClient(this)
        val geof = Geofence.Builder()
            .setRequestId(notesVM.photo.value)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setCircularRegion(latitude,longitude,radius)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val req = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geof)
        }.build()
        val intent = Intent(this, MyReciver::class.java)
        val pi = PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT)
        geofances.addGeofences(req,pi).run { addOnSuccessListener { Toast.makeText(applicationContext,"Dodano Geofence", Toast.LENGTH_LONG).show() } }
    }

    private fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION), 200)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 200)
            }
        }
    }


}
