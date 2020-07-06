package com.example.podroznik.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.podroznik.R

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_preference, MainPreference())
                .commit()
        }
    }


    class MainPreference : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_screen, rootKey)
        }
    }

}
