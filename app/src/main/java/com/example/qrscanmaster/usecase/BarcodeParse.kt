package com.example.qrscanmaster.usecase

import com.example.qrscanmaster.model.Barcode
import com.example.qrscanmaster.model.Wifi
import com.example.qrscanmaster.model.schema.Schema
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

object BarcodeParse {

    fun parseResult(result: Result):Barcode{
        val schema= parseSchema(result.barcodeFormat,result.text)
        return Barcode(
            text=result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date=result.timestamp,
        )
    }

    fun parseSchema(format: BarcodeFormat, text:String): Schema {
        if(format != BarcodeFormat.QR_CODE){

        }
        //revisar
        return Wifi.parse(text)!!

    }
}