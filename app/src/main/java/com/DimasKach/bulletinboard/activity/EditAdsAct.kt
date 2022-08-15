package com.DimasKach.bulletinboard.activity

import android.annotation.SuppressLint
import android.app.people.PeopleManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.adapters.ImageAdapter
import com.DimasKach.bulletinboard.databinding.ActivityEditAdsBinding
import com.DimasKach.bulletinboard.dialogs.DialogSpinnerHelper
import com.DimasKach.bulletinboard.fragments.FragmentCloseInterface
import com.DimasKach.bulletinboard.fragments.ImageListFragment
import com.DimasKach.bulletinboard.fragments.SelectImageItem
import com.DimasKach.bulletinboard.utils.CityHelper
import com.DimasKach.bulletinboard.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var isImagesPermissionGranted = false
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3)
                } else {

                    Toast.makeText(this, "Доступ не надано", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_IMAGES) {
            if(data != null){
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if(returnValues?.size!! > 1) {
                    binding.scrollViewMain.visibility = View.GONE
                    val fm = supportFragmentManager.beginTransaction()
                    fm.replace(R.id.place_holder, ImageListFragment(this, returnValues))
                    fm.commit()
                }
            }
        }
    }

    private fun init(){
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter

    }

    //OnClicks

    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if(binding.tvCity.text.toString() != getString(R.string.select_city)){
            binding.tvCity.text = getString(R.string.select_city)
        }
    }
    fun onClickSelectCity(view: View){
        val selectedCountry = binding.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)){
            val listCity = CityHelper.getAllCities( selectedCountry,this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, R.string.not_select_country, Toast.LENGTH_LONG).show()
        }
    }
    fun onClickGetImages(view: View){
        ImagePicker.getImages(this, 3)
    }

    override fun onFragClose(list: ArrayList<SelectImageItem>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
    }


}