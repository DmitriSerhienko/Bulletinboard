package com.DimasKach.bulletinboard.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.DimasKach.bulletinboard.activity.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val REQUEST_CODE_IMAGES = 999
    const val REQUEST_CODE_SINGLE_IMAGES = 998
    const val MAX_IMAGE_COUNT = 3
    fun getImages(context: AppCompatActivity, imageCounter: Int, rCode: Int){
        val options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
        Pix.start(context , options)
    }

    fun showSelectedImages(requestCode: Int, resultCode: Int, data: Intent?, edAct: EditAdsAct){
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValues?.size!! > 1 && edAct.chooseImageFragment == null) {
                    edAct.openChooseImageFrag(returnValues)
                } else if (returnValues.size == 1 && edAct.chooseImageFragment == null) {
                    //edAct.imageAdapter.update(returnValues)
                    //val tempList = ImageManager.getImageSize(returnValues[0])
                    CoroutineScope(Dispatchers.Main).launch {
                        edAct.binding.pBarLoad.visibility = View.VISIBLE
                        val bitMapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                        edAct.binding.pBarLoad.visibility = View.GONE
                        edAct.imageAdapter.update(bitMapArray)
                    }
                } else if (edAct.chooseImageFragment != null) {
                    edAct.chooseImageFragment?.upDateAdapter(returnValues)
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_SINGLE_IMAGES) {
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                edAct.chooseImageFragment?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
            }
        }
    }
}