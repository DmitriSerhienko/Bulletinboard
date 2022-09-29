package com.DimasKach.bulletinboard

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.DimasKach.bulletinboard.accounthelper.AccountHelper
import com.DimasKach.bulletinboard.activity.DescriptionActivity
import com.DimasKach.bulletinboard.activity.EditAdsAct
import com.DimasKach.bulletinboard.activity.FilterActivity
import com.DimasKach.bulletinboard.adapters.AdsRcAdapter
import com.DimasKach.bulletinboard.databinding.ActivityMainBinding
import com.DimasKach.bulletinboard.dialoghelper.DialogConst
import com.DimasKach.bulletinboard.dialoghelper.DialogHelper
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.utils.BillingManager
import com.DimasKach.bulletinboard.utils.FilterManager
import com.DimasKach.bulletinboard.viewmodel.FirebaseViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdsRcAdapter.Listener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    lateinit var googleSignInLauncher: ActivityResultLauncher <Intent>
    lateinit var filterLauncher: ActivityResultLauncher <Intent>
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    private var currentCategory: String? = null
    private var filter: String = "empty"
    private var filterDb: String = ""
    private var pref: SharedPreferences? = null
    private var isPremiumUser = false
    private var bManager: BillingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        isPremiumUser = pref?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!
        if(!isPremiumUser){
            initAds()
           // binding.mainContent.adView2.visibility = View.GONE - убираем рекламу для тестов
        } else {
            binding.mainContent.adView2.visibility = View.GONE
        }

        init()
        initRecyclerView()
        initViewModel()
        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_filter){
           val i = Intent(this@MainActivity, FilterActivity::class.java).apply {
               putExtra(FilterActivity.FILTER_KEY, filter)
           }
            filterLauncher.launch(i)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
        binding.mainContent.adView2.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mainContent.adView2.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mainContent.adView2.destroy()
        bManager?.closeConnection()
    }

    private fun initAds() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.mainContent.adView2.loadAd(adRequest)
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
    }
    private fun onActivityResultFilter(){
        filterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
                //Log.d("MyLog", "Filter: $filter")
                //Log.d("MyLog", "getFilter: ${FilterManager.getFilter(filter)}")
                filterDb = FilterManager.getFilter(filter)
            } else if(it.resultCode == RESULT_CANCELED){
                filterDb = ""
                filter = "empty"
            }
        }
    }


    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            val list = getAdsByCategory(it)
            if(!clearUpdate){
                adapter.upDateAdapter(list)
            }else {
                adapter.upDateAdapterWithClear(list)
            }

            binding.mainContent.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }
    private fun getAdsByCategory(list: ArrayList<Ad>): ArrayList<Ad>{
        val tempList = ArrayList<Ad>()
        tempList.addAll(list)
        if(currentCategory != getString(R.string.ad_def)){
            tempList.clear()
            list.forEach{
                if(currentCategory == it.category) tempList.add(it)
            }
        }
        tempList.reverse()
        return tempList
    }


    private fun init() {
        currentCategory = getString(R.string.ad_def)
        setSupportActionBar(binding.mainContent.toolbar)
        onActivityResult()
        navViewSettings()
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
        imAccount = binding.navView.getHeaderView(0).findViewById(R.id.imAccountImage)

    }

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bNavView.setOnItemSelectedListener { item ->
            clearUpdate = true
            when (item.itemId) {
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favs -> {
                    firebaseViewModel.loadMyFav()
                    mainContent.toolbar.title = getString(R.string.ad_fav)
                }
                R.id.id_home -> {
                    currentCategory = getString(R.string.ad_def)
                    firebaseViewModel.loadAllAdsFirstPage(filterDb)
                    mainContent.toolbar.title = getString(R.string.ad_def)
                }
            }
            true
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearUpdate = true
        when (item.itemId) {
            R.id.id_my_ads -> {
                Toast.makeText(this, "Pushed 1", Toast.LENGTH_LONG).show()
            }
            R.id.id_car -> {
                getAdsFromCat(getString(R.string.ad_car))
            }
            R.id.id_pc -> {
                getAdsFromCat(getString(R.string.ad_pc))
            }
            R.id.id_phone -> {
                getAdsFromCat(getString(R.string.ad_phone))
            }
            R.id.id_dm -> {
                getAdsFromCat(getString(R.string.ad_dm))
            }
            R.id.remove_ads -> {
                bManager = BillingManager(this)
                bManager?.startConnection()
            }
            R.id.ac_sing_up -> {
                dialogHelper.createSingDialog(DialogConst.SING_UP_STATE)
            }
            R.id.ac_sing_in -> {
                dialogHelper.createSingDialog(DialogConst.SING_IN_STATE)
            }
            R.id.ac_sing_out -> {
                if (mAuth.currentUser?.isAnonymous == true) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutG()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getAdsFromCat(cat: String){
        currentCategory = cat
        firebaseViewModel.loadAllAdsFromCat(cat, filterDb)
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    tvAccount.setText(R.string.anonymous_enter)
                    imAccount.setImageResource(R.drawable.ic_account_def)
                }
            })
        } else if (user.isAnonymous) {
            tvAccount.setText(R.string.anonymous_enter)
            imAccount.setImageResource(R.drawable.ic_account_def)
        } else if(!user.isAnonymous){
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionActivity::class.java)
        i.putExtra("AD", ad)
        startActivity(i)
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }

    private fun navViewSettings() = with(binding){
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.adsCat)
        val spanAdsCat = SpannableString(adsCat.title)
        spanAdsCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity,
            R.color.dark_red)), 0, adsCat.title.length, 0)
        adsCat.title = spanAdsCat

        val aссCat = menu.findItem(R.id.accCat)
        val spanAссCat = SpannableString(aссCat.title)
        spanAссCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity,
            R.color.dark_red)), 0, aссCat.title.length, 0)
        aссCat.title = spanAссCat
    }

    private fun scrollListener() = with(binding.mainContent) {
        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recView, newState)
                if (!recView.canScrollVertically(SCROLL_DOWN)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value!!
                    if (adsList.isNotEmpty()) {
                        getAdsFromCats(adsList)
                    }
                }
            }
        })
    }

    private fun getAdsFromCats(adsList: ArrayList<Ad>){
        adsList[0].let {
            if (currentCategory != getString(R.string.ad_def)) {
                firebaseViewModel.loadAllAdsFromCatNextPage(it.category!!, it.time!!, filterDb)
            } else {
                firebaseViewModel.loadAllAdsNextPage(it.time!!, filterDb)
            }
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = 1
    }

}