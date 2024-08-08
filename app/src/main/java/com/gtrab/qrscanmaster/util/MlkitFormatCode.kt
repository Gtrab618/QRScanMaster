package com.gtrab.qrscanmaster.util

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat

fun Barcode.getFormatString(formatId : Int):BarcodeFormat{
    return when(formatId){
        4096 -> BarcodeFormat.AZTEC
        8 -> BarcodeFormat.CODABAR
        2 -> BarcodeFormat.CODE_39
        4 -> BarcodeFormat.CODE_93
        1 -> BarcodeFormat.CODE_128
        16 -> BarcodeFormat.DATA_MATRIX
        64 -> BarcodeFormat.EAN_8
        32 -> BarcodeFormat.EAN_13
        128 -> BarcodeFormat.ITF
        256 -> BarcodeFormat.QR_CODE
        512 -> BarcodeFormat.UPC_A
        1024 -> BarcodeFormat.UPC_E

      else ->BarcodeFormat.UPC_EAN_EXTENSION
    }

}