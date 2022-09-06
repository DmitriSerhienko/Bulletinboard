package com.DimasKach.bulletinboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    public fun loadAllAds (){
        dbManager.readDataFromDb(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })

    }

}