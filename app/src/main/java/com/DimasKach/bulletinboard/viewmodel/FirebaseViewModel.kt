package com.DimasKach.bulletinboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAdsFirstPage (filter: String){
        dbManager.getAllAdsFirstPage(filter, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
    fun loadAllAdsNextPage (time: String, filter: String){
        dbManager.getAllAdsNextPage(time, filter, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCat (cat: String, filter: String){
        dbManager.getAllAdsFromCatFirstPage(cat, filter,  object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
    fun loadAllAdsFromCatNextPage (cat: String, time: String, filter: String){
        dbManager.getAllAdsFromCatNextPage(cat, time, filter, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
    fun onFavClick(ad:Ad){
        dbManager.onFavClick(ad, object: DbManager.FinishWorkListener{
            override fun onFinish( isDone: Boolean) {
                val updateList = liveAdsData.value
                val pos = updateList?.indexOf(ad)
                if(pos != -1){
                    pos?.let{
                        val favCounter = if(ad.isFav) ad.favCounter!!.toInt() - 1 else ad.favCounter!!.toInt() + 1
                        updateList[pos] = updateList[pos].copy(isFav = !ad.isFav, favCounter = favCounter.toString())
                    }
                }
                liveAdsData.postValue(updateList)
            }
        })
    }

    fun adViewed(ad: Ad){
        dbManager.adViewed(ad)
    }

    fun loadMyAds (){
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
    fun loadMyFav (){
        dbManager.getMyFav(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteItem(ad: Ad){
        dbManager.deleteAd(ad, object: DbManager.FinishWorkListener{
            override fun onFinish(isDone: Boolean) {
                val updateList = liveAdsData.value
                updateList?.remove(ad)
                liveAdsData.postValue(updateList)
            }
        })
    }

}