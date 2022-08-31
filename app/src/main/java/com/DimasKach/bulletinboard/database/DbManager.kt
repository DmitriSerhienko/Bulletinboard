package com.DimasKach.bulletinboard.database

import com.DimasKach.bulletinboard.data.Ad
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database("https://bulettinboard-default-rtdb.europe-west1.firebasedatabase.app/").getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Ad){
        if(auth.uid != null ) db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad)
    }

}