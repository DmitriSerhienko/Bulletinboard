package com.DimasKach.bulletinboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_my_ads -> {
                Toast.makeText(this, "Pushed 1", Toast.LENGTH_LONG).show()
            }
            R.id.id_car -> {
                Toast.makeText(this, "Pushed 2", Toast.LENGTH_LONG).show()
            }
            R.id.id_pc -> {

            }
            R.id.id_phone -> {

            }
            R.id.id_dm -> {

            }
            R.id.ac_sing_up -> {
                Toast.makeText(this, "Pushed 4", Toast.LENGTH_LONG).show()
            }
            R.id.ac_sing_in -> {
                Toast.makeText(this, "Pushed 5", Toast.LENGTH_LONG).show()
            }
            R.id.ac_sing_out -> {

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}