package com.gtrab.qrscanmaster.extension

import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema

fun BarcodeSchema.toImageId(): Int?{
    return when (this){
        BarcodeSchema.WIFI -> R.drawable.hs_wifi
        BarcodeSchema.URL -> R.drawable.hs_link
        else -> null
    }
}