package com.example.qrscanmaster.usecase

import com.example.qrscanmaster.model.Barcode
import com.example.qrscanmaster.model.schema.Schema
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

object BarcodeParse {

    fun parseResult(result: Result):Barcode{
        val schema= parseSchema()
        return Barcode(
            text=result.text,
            date=result.timestamp,
            formattedText =
        )
    }

    fun parseSchema(format: BarcodeFormat, text:String):Schema {
        if(format != BarcodeFormat.QR_CODE){

        }
        return Wifi.parse(text)
    }
}