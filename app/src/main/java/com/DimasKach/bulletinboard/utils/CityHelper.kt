package com.DimasKach.bulletinboard.utils

import java.io.IOException
import java.io.InputStream
import android.content.Context
import org.json.JSONObject


object CityHelper {
    fun getAllCountries(context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()
        try {

            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countryNames = jsonObject.names()
            if (countryNames != null) {
                for (n in 0 until countryNames.length()) {
                    tempArray.add(countryNames.getString(n))
                }
            }


        } catch (e: IOException) {

        }
        return tempArray
    }
}