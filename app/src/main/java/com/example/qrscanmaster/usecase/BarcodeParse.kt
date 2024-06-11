package com.example.qrscanmaster.usecase

import com.example.qrscanmaster.model.Barcode
import com.google.zxing.Result

object BarcodeParse {

    fun parseResult(result: Result):Barcode{
        val schema= parseSchema()
        return Barcode(
            text=result.text,
            date=result.timestamp,
            formattedText = sche
        )
    }

    fun parseSchema(){

    }
}