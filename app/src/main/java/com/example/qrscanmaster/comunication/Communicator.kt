package com.example.qrscanmaster.comunication

import com.example.qrscanmaster.model.QRCodeResult

interface Communicator {
    fun passInfoQr(data : QRCodeResult?)
}