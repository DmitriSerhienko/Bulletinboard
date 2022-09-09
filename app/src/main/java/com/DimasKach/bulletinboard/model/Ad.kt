package com.DimasKach.bulletinboard.model

import java.io.Serializable

data class Ad(
    val country : String? = null,
    val city : String? = null,
    val tel : String? = null,
    val index : String? = null,
    val withSent : String? = null,
    val category : String? = null,
    val title : String? = null,
    val price : String? = null,
    val description : String? = null,
    val key: String? = null,
    val uid: String? = null,

    val viewsCounter: String? = "0",
    val emailsCounter: String? = "0",
    val callsCounter: String? = "0"
): Serializable
