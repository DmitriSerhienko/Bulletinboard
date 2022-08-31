package com.DimasKach.bulletinboard.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database("https://bulettinboard-default-rtdb.europe-west1.firebasedatabase.app/").getReference("main")

    fun publishAd(){
        db.setValue("Hello11")
    }
}