package com.example.podroznik.views

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.podroznik.adapters.PhotosAdapter
import com.example.podroznik.R
import com.example.podroznik.databinding.ActivityMainBinding
import com.example.podroznik.viewmodels.MainVM
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val mainVM by lazy { ViewModelProvider(this).get(MainVM::class.java) }
    private val adapter by lazy { PhotosAdapter() }
    private lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBindingUtil =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        mainRecyclerView.let {
            it.layoutManager = GridLayoutManager(this, 2)
            it.adapter = this.adapter
        }

        dataBindingUtil.let {
            it.mainVM = mainVM
            it.lifecycleOwner = this
        }

        mainVM.notes.observe({ this.lifecycle }) {
            runOnUiThread {
                adapter.setNotes(it)
            }
        }


        mainVM.getGoNext().observe({ this.lifecycle }) {
            if (it) {
                val intent = Intent(this, NoteActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }
        checkPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.item_preference){
            startActivity(Intent(this,PreferenceActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.hasExtra("note")) {
            val note = data.getSerializableExtra("note")
            mainVM.insertNote(note)
        }
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 200)
            }
        }
    }
}