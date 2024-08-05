package com.gtrab.qrscanmaster.comunication

import com.gtrab.qrscanmaster.model.Barcode

interface Communicator {
    fun passInfoQr(barcode: Barcode )
}