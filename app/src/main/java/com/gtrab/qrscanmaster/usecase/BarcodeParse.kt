package com.gtrab.qrscanmaster.usecase

import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.Wifi
import com.gtrab.qrscanmaster.model.schema.Schema
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

object BarcodeParse {

    fun parseResult(result: Result):Barcode{
        val schema= parseSchema(result.barcodeFormat,result.text)
        return Barcode(
            text=result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date=result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text:String): Schema {
        if(format != BarcodeFormat.QR_CODE){

        }
        //revisar
        return Wifi.parse(text)!!

    }
}