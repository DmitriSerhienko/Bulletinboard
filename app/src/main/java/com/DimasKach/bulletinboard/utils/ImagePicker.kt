package com.DimasKach.bulletinboard.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.activity.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply{
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "/pix/images"
        }
        return options
    }

    fun getMultiImages(edAct: EditAdsAct, imageCounter: Int) {
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectImage(edAct, result.data)
                    closePixFrag(edAct)
                }
            }
            //PixEventCallback.Status.BACK_PRESSED ->
        }
    }

    fun addImages(edAct: EditAdsAct, imageCounter: Int) {
        val f = edAct.chooseImageFragment
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFragment = f
                    openChooseImageFrag(edAct, f!!)
                    edAct.chooseImageFragment?.upDateAdapter(result.data as ArrayList<Uri>, edAct)
                }
            }
        }
    }
    fun getSingleImages(edAct: EditAdsAct) {
        val f = edAct.chooseImageFragment
        edAct.addPixToActivity(R.id.place_holder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFragment = f
                    openChooseImageFrag(edAct, f!!)
                    SingleImage(edAct, result.data[0])
                }
            }
            //PixEventCallback.Status.BACK_PRESSED ->
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsAct, f: Fragment){
        edAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder, f).commit()
    }

    private fun closePixFrag(edAct: EditAdsAct ) {

        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }


    fun getMultiSelectImage(edAct: EditAdsAct, uris: List<Uri>) {

        if (uris?.size!! > 1 && edAct.chooseImageFragment == null) {
            edAct.openChooseImageFrag(uris as ArrayList<Uri>)
        }  else if (uris.size == 1 && edAct.chooseImageFragment == null) {
            //edAct.imageAdapter.update(returnValues)
            //val tempList = ImageManager.getImageSize(returnValues[0])
            CoroutineScope(Dispatchers.Main).launch {
                edAct.binding.pBarLoad.visibility = View.VISIBLE
                val bitMapArray =
                    ImageManager.imageResize(uris as ArrayList<Uri>, edAct) as ArrayList<Bitmap>
                edAct.binding.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.update(bitMapArray)
                closePixFrag(edAct)
            }
        }
    }

    private fun SingleImage(edAct: EditAdsAct, uri: Uri){

        edAct.chooseImageFragment?.setSingleImage(uri, edAct.editImagePos)

    }

}