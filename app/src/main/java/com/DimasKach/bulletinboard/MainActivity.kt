package com.DimasKach.bulletinboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.DimasKach.bulletinboard.activity.EditAdsAct
import com.DimasKach.bulletinboard.adapters.AdsRcAdapter
import com.DimasKach.bulletinboard.databinding.ActivityMainBinding
import com.DimasKach.bulletinboard.dialoghelper.DialogConst
import com.DimasKach.bulletinboard.dialoghelper.DialogHelper
import com.DimasKach.bulletinboard.dialoghelper.GoogleAccConst
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdsRcAdapter.DeleteItemListener/*, ReadDataCallback - используем если идем без архитектуры  MVVM*/ {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
//    val dbManager = DbManager(this) - используем если идем без архитектуры  MVVM
    val adapter = AdsRcAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRecyclerView()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
//        dbManager.readDataFromDb() - используем если идем без архитектуры  MVVM
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null) {
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }

            } catch(e:ApiException){
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initViewModel (){
        firebaseViewModel.liveAdsData.observe(this, {
            adapter.upDateAdapter(it)
        })

    }
    private fun init() {
        setSupportActionBar(binding.mainContent.toolbar)
        val toggle =
            ActionBarDrawerToggle(this,
                binding.drawerLayout,
                binding.mainContent.toolbar,
                R.string.open,
                R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }
    private fun bottomMenuOnClick() = with(binding){
        mainContent.bNavView.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity,EditAdsAct::class.java)
                    startActivity(i)
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favs -> {
                    Toast.makeText(this@MainActivity, "Favourite", Toast.LENGTH_LONG).show()
                }
                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.ad_def)
                }
            }
            true
        }
    }

    private fun initRecyclerView(){
        binding.apply{
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
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
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutG()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate (user: FirebaseUser?){
        tvAccount.text = if (user == null){
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }

//    override fun readData(list: List<Ad>) {  - используем если идем без архитектуры  MVVM
//        adapter.upDateAdapter(list)
//    }
    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

}