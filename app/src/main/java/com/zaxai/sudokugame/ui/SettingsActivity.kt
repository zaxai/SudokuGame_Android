package com.zaxai.sudokugame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat
import com.zaxai.sudokugame.R

class SettingsActivity : AppCompatActivity() {
    class SettingsFragment:PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences,rootKey)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction().replace(R.id.settings,SettingsFragment()).commit()
        }
        val toolbar:Toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return true
    }
}