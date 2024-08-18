package com.gtrab.qrscanmaster.usecase

import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.schema.Wifi
import com.gtrab.qrscanmaster.model.schema.Schema
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import com.gtrab.qrscanmaster.model.schema.App
import com.gtrab.qrscanmaster.model.schema.BoardingPass
import com.gtrab.qrscanmaster.model.schema.Geo
import com.gtrab.qrscanmaster.model.schema.Other
import com.gtrab.qrscanmaster.model.schema.Sms
import com.gtrab.qrscanmaster.model.schema.Url
import com.gtrab.qrscanmaster.model.schema.VCard

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

    private fun parseSchema(format: BarcodeFormat, text:String): Schema {
        if(format != BarcodeFormat.QR_CODE){

        }
        return App.parse(text)
            ?: Wifi.parse(text)
            ?: Url.parse(text)
            ?: Geo.parse(text)
            ?: Sms.parse(text)
            ?: VCard.parse(text)
            ?: BoardingPass.parse(text)
            ?: Other(text)

    }
}