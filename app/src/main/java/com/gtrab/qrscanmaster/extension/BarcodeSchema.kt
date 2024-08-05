package com.gtrab.qrscanmaster.extension

import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema

fun BarcodeSchema.toImageId(): Int?{
    return if (this == BarcodeSchema.WIFI) {
        R.drawable.galery
    } else {
        null
    }
}