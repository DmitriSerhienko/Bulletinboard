package com.DimasKach.bulletinboard.utils

import java.io.IOException
import java.io.InputStream
import android.content.Context
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


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
    fun getAllCities(country: String, context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)
                for (n in 0 until cityNames.length()) {
                    tempArray.add(cityNames.getString(n))
                }
        } catch (e: IOException) {
        }
        return tempArray
    }
    fun filterListData(list: ArrayList<String>, searchText: String?): ArrayList<String>{
        val tempList = ArrayList<String>()
        tempList.clear()
        if(searchText == null) {
            tempList.add("No Result")
            return tempList
        }
        for(selection: String in list){
            if(selection.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT)))
                tempList.add(selection)
        }
        if(tempList.size == 0) tempList.add("No result")
        return tempList
    }

}