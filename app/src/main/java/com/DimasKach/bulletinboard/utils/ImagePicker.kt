package com.DimasKach.bulletinboard.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

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
}