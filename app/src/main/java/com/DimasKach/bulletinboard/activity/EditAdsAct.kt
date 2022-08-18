package com.DimasKach.bulletinboard.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.adapters.ImageAdapter
import com.DimasKach.bulletinboard.databinding.ActivityEditAdsBinding
import com.DimasKach.bulletinboard.dialogs.DialogSpinnerHelper
import com.DimasKach.bulletinboard.fragments.FragmentCloseInterface
import com.DimasKach.bulletinboard.fragments.ImageListFragment
import com.DimasKach.bulletinboard.utils.CityHelper
import com.DimasKach.bulletinboard.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    private var chooseImageFragment: ImageListFragment? = null
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var isImagesPermissionGranted = false
    private lateinit var imageAdapter: ImageAdapter
    var editImagePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_IMAGES)
                } else {

                    Toast.makeText(this, "Доступ не надано", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValues?.size!! > 1 && chooseImageFragment == null) {
                    openChooseImageFrag(returnValues)
                } else if (returnValues.size == 1 && chooseImageFragment == null) {
                    imageAdapter.update(returnValues)
                } else if (chooseImageFragment != null) {
                    chooseImageFragment?.upDateAdapter(returnValues)
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_SINGLE_IMAGES){
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                chooseImageFragment?.setSingleImage(uris?.get(0)!!, editImagePos)
            }
        }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter

    }

    //OnClicks

    fun onClickSelectCountry(view: View) {
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if (binding.tvCity.text.toString() != getString(R.string.select_city)) {
            binding.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, R.string.not_select_country, Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGetImages(view: View) {
        if(imageAdapter.mainArray.size == 0 ){
            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_IMAGES)
        } else{
            openChooseImageFrag(imageAdapter.mainArray)
        }

    }

    override fun onFragClose(list: ArrayList<String>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    private fun openChooseImageFrag(newList: ArrayList<String>){
        chooseImageFragment = ImageListFragment(this, newList)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }


}