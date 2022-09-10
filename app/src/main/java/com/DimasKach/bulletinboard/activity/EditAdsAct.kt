package com.DimasKach.bulletinboard.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.adapters.ImageAdapter
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.model.DbManager
import com.DimasKach.bulletinboard.databinding.ActivityEditAdsBinding
import com.DimasKach.bulletinboard.dialogs.DialogSpinnerHelper
import com.DimasKach.bulletinboard.fragments.FragmentCloseInterface
import com.DimasKach.bulletinboard.fragments.ImageListFragment
import com.DimasKach.bulletinboard.utils.CityHelper
import com.DimasKach.bulletinboard.utils.ImagePicker
import com.fxn.utility.PermUtil

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    var chooseImageFragment: ImageListFragment? = null
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    var isImagesPermissionGranted = false
    lateinit var imageAdapter: ImageAdapter
    private var isEditState = false
    private var ad: Ad? = null
    var editImagePos = 0
    private val dbManager = DbManager()
    var launcherMultiselectImage: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkEditState()
    }

    private fun checkEditState(){
        isEditState = isEditState()
        if(isEditState){
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if (ad!= null) fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)

    }
    private fun fillViews(ad: Ad) = with(binding){
        tvCountry.text = ad.country
        tvCity.text = ad.city
        editTel.setText(ad.tel)
        editIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSent.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        editDescription.setText(ad.description)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {

        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_IMAGES)
                } else {

                    Toast.makeText(this, "Доступ не надано", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    private fun init() {
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
        launcherMultiselectImage = ImagePicker.getLauncherForMultiSelectImage(this)
        launcherSingleSelectImage = ImagePicker.getLauncherForSingleImage(this)
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

    fun onClickSelectCat(view: View) {
        val listCat = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this, listCat, binding.tvCat)
    }

    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0) {
            ImagePicker.launcher(this, launcherMultiselectImage, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageFragment?.upDateAdapterFromEdit(imageAdapter.mainArray)
        }

    }

    fun onClickPublish(view: View){
        val adTemp = fillAd()
        if (isEditState) {
            dbManager.publishAd(adTemp.copy(key = ad?.key), onPublishFinish() )
        } else {
            dbManager.publishAd(adTemp, onPublishFinish())
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener{
        return  object: DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillAd(): Ad{
        val ad: Ad
        binding.apply {
            ad = Ad(
                tvCountry.text.toString(),
                tvCity.text.toString(),
                editTel.text.toString(),
                editIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                tvCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                editDescription.text.toString(),
                dbManager.db.push().key,
                "0",
                dbManager.auth.uid
            )
        }
        return ad
    }


    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    fun openChooseImageFrag(newList: ArrayList<String>?) {
        chooseImageFragment = ImageListFragment(this, newList)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }


}