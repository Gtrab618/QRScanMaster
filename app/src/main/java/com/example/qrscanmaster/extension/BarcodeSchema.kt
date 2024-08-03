package com.example.qrscanmaster.extension

import com.example.qrscanmaster.R
import com.example.qrscanmaster.model.Barcode
import com.example.qrscanmaster.model.schema.BarcodeSchema

fun BarcodeSchema.toImageId(): Int?{
    return if (this == BarcodeSchema.WIFI) {
        R.drawable.galery
    } else {
        null
    }
}