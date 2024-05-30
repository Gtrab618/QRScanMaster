package com.example.qrscanmaster.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import com.example.qrscanmaster.model.QRCodeResult
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.detector.Detector
import kotlin.math.min

fun Context.openAppSettings(){
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data= Uri.parse("package:$packageName")

    }.let(::startActivity)
}

fun decodeQRCode(bitmap: Bitmap): QRCodeResult? {
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
        // Si se decodifica, intenta encontrar el área del QR para recortarlo
        val detectorResult = Detector(binaryBitmap.blackMatrix).detect(hints)
        // Encuentra los límites del QR en la imagen
        val points=detectorResult.points
        for ((index, point) in points.withIndex()) {
            println("Punto $index: (${point.x}, ${point.y})")
        }
        println("test")
        val minX = points.minOf { it.x.toInt()-68 }
        val minY = points.minOf { it.y.toInt()-68 }
        val maxX = points.maxOf { it.x.toInt()+68 }
        val maxY = points.maxOf { it.y.toInt()+68 }
        // recortar el area de QR
        val widthQrCut= min(maxX-minX,bitmap.width)
        val heightQrCut= min(maxY-minY,bitmap.height)
        val croppedBitmap = Bitmap.createBitmap(bitmap, minX, minY, widthQrCut, heightQrCut)


         QRCodeResult(result.text,croppedBitmap)
    } catch (e: NotFoundException) {
        // No se encontró ningún código QR en la imagen
        null
    }
}

