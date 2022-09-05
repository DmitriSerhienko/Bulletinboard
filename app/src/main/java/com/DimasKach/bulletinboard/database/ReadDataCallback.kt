package com.DimasKach.bulletinboard.database

import com.DimasKach.bulletinboard.data.Ad

interface ReadDataCallback {
    fun readData(list: List<Ad>)
}