package com.example.qrscanmaster.comunication

import com.example.qrscanmaster.model.Barcode
import com.google.zxing.Result

interface Communicator {
    fun passInfoQr(barcode: Barcode )
}