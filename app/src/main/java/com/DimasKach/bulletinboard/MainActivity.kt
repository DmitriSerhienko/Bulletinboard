package com.DimasKach.bulletinboard

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.DimasKach.bulletinboard.databinding.ActivityMainBinding
import com.DimasKach.bulletinboard.dialoghelper.DialogConst
import com.DimasKach.bulletinboard.dialoghelper.DialogHelper
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val toggle =
            ActionBarDrawerToggle(this,
                binding.drawerLayout,
                binding.mainContent.toolbar,
                R.string.open,
                R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
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
                dialogHelper.createSingDialog(DialogConst.SING_UP_STATE)
            }
            R.id.ac_sing_in -> {
                dialogHelper.createSingDialog(DialogConst.SING_IN_STATE)
            }
            R.id.ac_sing_out -> {

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}