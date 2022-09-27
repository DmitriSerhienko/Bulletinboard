package com.DimasKach.bulletinboard.utils

import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.model.AdFilter
import java.lang.StringBuilder

object FilterManager {
    fun createFilter(ad: Ad): AdFilter{
        return AdFilter(
            ad.time,
            "${ad.category}_${ad.time}",

            "${ad.category}_${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.withSent}_${ad.time}",

            "${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.withSent}_${ad.time}"

        )
    }
    fun getFilter(filter: String): String{
        val sBuilder = StringBuilder()
        val tempArray = filter.split("_")
        if(tempArray[0] != "empty") sBuilder.append("country_")
        if(tempArray[1] != "empty") sBuilder.append("city_")
        if(tempArray[2] != "empty") sBuilder.append("index_")
        sBuilder.append("withSent_time")

        return sBuilder.toString()
    }
}