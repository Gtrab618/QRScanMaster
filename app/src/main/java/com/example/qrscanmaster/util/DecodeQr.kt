package com.example.qrscanmaster.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer

fun Context.openAppSettings(){
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data= Uri.parse("package:$packageName")

    }.let(::startActivity)
}

fun decodeQRCode(bitmap: Bitmap): Result? {
    //leer codigo en varios formatos
    val multiFormatReader = MultiFormatReader()
    //paso la lista de que codigo qr deberia buscar
    val hints = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
    )

    val source = RGBLuminanceSource(bitmap.width, bitmap.height, IntArray(bitmap.width * bitmap.height).apply {
        bitmap.getPixels(this, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    })
    //imagen binaria que el lector puede procesar
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        val result = multiFormatReader.decode(binaryBitmap, hints)

         result
    } catch (e: NotFoundException) {
        // No se encontró ningún código QR en la imagen
        null
    }
}

